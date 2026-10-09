# Cross-build release specification

This directory is the source of truth for host/target packaging. Build orchestration will be written in Python 3.11+ using the standard library (`tomllib`, `subprocess`, `urllib`, `zipfile`, and `tarfile`). Python is available on the GitHub-hosted runners and keeps provisioning scripts usable on Windows, macOS, and Linux without adding a Python package dependency.

The repository currently has no Gradle build, Kotlin compiler entry point, or native FFI bridge. Consequently this file specifies the build contract; release workflows should be added when there is a real build command for them to run. They must not publish placeholder archives as compiler artifacts.

## Matrix

`matrix.toml` defines six host OS/architecture profiles and six target profiles. The valid initial action set contains 31 pairs: all 30 Linux/Windows target combinations plus the native macOS-host to Darwin-target pair. Apple SDK terms restrict use of that SDK to Apple-branded macOS hardware, so the public matrix does not cross-build Darwin from Linux or Windows. Target ABI dependencies are attached to target profiles; native ABI dependencies are attached to the host package. A pair's dependency set is the union of its host and target requirements.

Linux FFI shared libraries need extra care. A shared object loaded into a JVM/Python process must match that process's libc. Each Linux host package therefore needs glibc and musl bridge variants if both JVM environments are in scope. The standalone Clang/LLD executables can be built as static musl executables to avoid a glibc dependency. This libc split does not create extra host-target pairs; it creates two FFI bridge files inside each Linux host package.

The target profiles record a Windows ABI decision and a macOS SDK distribution gate:

* **Windows:** Use the GNU Windows ABI with MinGW-w64 headers/startup objects and UCRT. LLVM-MinGW publishes Linux cross-toolchains for Windows x86_64 and AArch64, and documents UCRT as its primary runtime choice. UCRT is present in Windows 10 and later, which becomes our minimum Windows target. Microsoft permits redistribution of specified Visual Studio files but says not all files can be redistributed, so the MSVC sysroot remains deferred pending file-by-file license review. This is a conservative packaging decision, not a legal determination. See [Microsoft's redistribution list](https://learn.microsoft.com/en-us/visualstudio/releases/2026/redistribution), [DLL redistribution guidance](https://learn.microsoft.com/en-us/cpp/windows/determining-which-dlls-to-redistribute?view=msvc-170), and [LLVM-MinGW](https://github.com/mstorsjo/llvm-mingw).
* **macOS:** normal hosted C programs need macOS SDK headers and `libSystem` link stubs. Apple's [Xcode and SDK agreement](https://www.apple.com/legal/sla/docs/xcode.pdf) restricts SDK use to Apple-branded Macs and does not grant redistribution rights, so the package cannot include the SDK and a Darwin build cannot run legally on a non-Apple host. Apple offers [Command Line Tools for Xcode](https://developer.apple.com/documentation/xcode/installing-the-command-line-tools) separately from the full Xcode app; that installer includes the same macOS SDK and toolchain. Therefore a Mac user can avoid installing the full Xcode app, but must install Command Line Tools or supply an already licensed SDK. The compiler will report a missing-SDK error when neither is available. This is not a fully offline macOS target.

Linux targets are the first fully specified target family. Build each musl sysroot from the musl source and build the matching compiler-rt builtins from the pinned LLVM source. Pin source revisions, checksums, and build options in the eventual lock file; never resolve `latest` during a release build.

## Provisioning sources

| Component | Provision from | Notes |
|---|---|---|
| Kotlin/JVM compiler application | This repository's Gradle distribution task (to be added) | The JAR is host-independent. The build currently does not exist. |
| JNI/FFI bridge headers and JVM ABI | Eclipse Temurin JDK from [Adoptium](https://adoptium.net/temurin/releases/) in CI | Build one bridge per native host ABI. The end-user JVM remains a runtime requirement. |
| Clang, LLVM backends, LLD, compiler-rt | Pinned release/source from [llvm/llvm-project](https://github.com/llvm/llvm-project) | Build a host-native tool bundle with X86, AArch64, and RISCV backends. |
| Linux C runtime | Pinned source from [musl](https://git.musl-libc.org/cgit/musl/) | Build headers, startup objects, libc, and linker metadata for x86_64, AArch64, and RISC-V 64. Record the RISC-V ISA/ABI baseline in the target profile. |
| Windows GNU/UCRT target sysroot | Pinned [LLVM-MinGW](https://github.com/mstorsjo/llvm-mingw), [mingw-w64](https://www.mingw-w64.org/), and LLVM sources | Build x86_64 and AArch64 target headers, startup objects, import libraries, and compiler-rt. Target Windows 10 or later, where UCRT is present. |
| Deferred Windows MSVC sysroot | Windows SDK and Visual Studio Build Tools components from Microsoft's official distribution channels | Do not include in release packages until a file-by-file redistribution review clears all shipped headers, libraries, and runtime files. |
| macOS SDK | Apple Command Line Tools or Xcode on a macOS runner/user machine | Use the SDK locally for Darwin compilation; never include it in release artifacts. Users need Command Line Tools or another valid local SDK installation. |

## Host build requirements

| Host profile | GitHub Actions runner | Host-specific build inputs |
|---|---|---|
| `linux-x86_64` | `ubuntu-24.04` | JDK; Linux-native FFI bridge built for glibc and musl; static-musl host tools. |
| `linux-aarch64` | `ubuntu-24.04-arm` | JDK; AArch64 FFI bridge built for glibc and musl; static-musl host tools. |
| `linux-riscv64` | No hosted RISC-V runner currently listed | Cross-build the host tools and bridge from an x64/ARM64 Linux runner with a pinned RISC-V sysroot; execute smoke tests under QEMU or on a self-hosted RISC-V runner. |
| `macos-aarch64` | `macos-15` (ARM64) | JDK; native Mach-O FFI bridge and host tools. Use the runner SDK for macOS-to-Darwin builds; never package it. |
| `windows-x86_64` | `windows-2025` | JDK; native PE/COFF FFI bridge and host tools. |
| `windows-aarch64` | `windows-11-arm` | JDK; native ARM64 PE/COFF FFI bridge and host tools. |

Runner labels are an initial mapping and must be checked against the repository's GitHub plan when workflows are enabled. GitHub's runner reference currently lists Linux, Windows, and macOS hosted runners for x64 and ARM64; it does not list a RISC-V hosted runner.

## Pair scripts and artifacts

When implementation begins, add one small entry script per pair under:

```text
scripts/pairs/<host-id>/<target-id>.py
```

Each entry script delegates to shared Python modules in `scripts/lib/`; provisioning, cache keys, checksum validation, diagnostics, and archive layout must not be copied 36 times. Each pair script reads its host and target records from `matrix.toml`, provisions only pinned inputs, invokes the Gradle distribution task, and stages exactly one host-target package.

The staged directory layout is identical for every pair (native file suffixes vary by OS):

```text
package/
  manifest.json
  bin/clang[.exe]
  bin/ld.lld[.exe]
  bin/llvm-ar[.exe]
  native/<host-id>/libtinycc.<dll|so|dylib>
  targets/<target-id>/profile.json
  targets/<target-id>/sysroot/...
  licenses/
```

The release asset name is `tinycc-<tag>-<host-id>--<target-id>.zip`. Target sysroot content is kept under `targets/`; the FFI caller first selects the native library by its own process OS/architecture/libc, then requests a target profile from the installed packages. If artifacts are installed separately, users install the host package plus the requested target package. The all-in-one JAR/distribution may embed every host bridge and target sysroot for offline use.

## Manual release workflow contract

Once the Gradle task and pair builder exist, create a manually dispatched workflow for each pair. Every workflow must:

1. Require an existing Git tag as input and check out that exact tag.
2. Build one pair package and upload it as a uniquely named CI artifact.
3. Serialize release updates using a concurrency group keyed by repository and tag.
4. Create the GitHub Release for the tag if it does not exist, then upload the pair ZIP to that same release with `--clobber` for reruns.
5. Request only `contents: write`; use the built-in `GITHUB_TOKEN`.

The commit tag is the release version. Workflow dispatch from an arbitrary branch must not publish a release asset.

