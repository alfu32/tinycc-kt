# Cross-build release specification

This directory is the source of truth for host/target packaging. Build orchestration will be written in Python 3.11+ using the standard library (`tomllib`, `subprocess`, `urllib`, `zipfile`, and `tarfile`). Python is available on the GitHub-hosted runners and keeps provisioning scripts usable on Windows, macOS, and Linux without adding a Python package dependency.

The repository currently has no Gradle build, Kotlin compiler entry point, or native FFI bridge. Consequently this file specifies the build contract; release workflows should be added when there is a real build command for them to run. They must not publish placeholder archives as compiler artifacts.

## Matrix

`matrix.toml` defines six host OS/architecture profiles and six target profiles. The intended initial action set is the valid Cartesian product, 6 × 6 = 36 host-target pairs. Target ABI dependencies are attached to target profiles; native ABI dependencies are attached to the host package. A pair's dependency set is the union of its host and target requirements.

Linux FFI shared libraries need extra care. A shared object loaded into a JVM/Python process must match that process's libc. Each Linux host package therefore needs glibc and musl bridge variants if both JVM environments are in scope. The standalone Clang/LLD executables can be built as static musl executables to avoid a glibc dependency. This libc split does not create extra host-target pairs; it creates two FFI bridge files inside each Linux host package.

The first target list intentionally records two unresolved release gates:

* **Windows/MSVC:** Clang's MSVC target can be cross-linked from Linux with `lld-link`; the target sysroot needs Windows SDK, UCRT, and Visual C++ runtime headers/libraries. Microsoft redistribution rights must be checked for every shipped file. Keep MinGW-w64 + UCRT as the fallback profile if the required MSVC files cannot be included in the offline distribution.
* **macOS:** cross-linking normal hosted C programs needs macOS SDK headers and `libSystem` link stubs. GitHub's macOS runner can supply an SDK for CI, but that does not grant permission to place the SDK in our release. Until an allowed, project-owned replacement sysroot exists, the macOS target is marked `sdk_gate`; it cannot honestly be advertised as out-of-the-box on non-Mac hosts.

Linux targets are the first fully specified target family. Build each musl sysroot from the musl source and build the matching compiler-rt builtins from the pinned LLVM source. Pin source revisions, checksums, and build options in the eventual lock file; never resolve `latest` during a release build.

## Provisioning sources

| Component | Provision from | Notes |
|---|---|---|
| Kotlin/JVM compiler application | This repository's Gradle distribution task (to be added) | The JAR is host-independent. The build currently does not exist. |
| JNI/FFI bridge headers and JVM ABI | Eclipse Temurin JDK from [Adoptium](https://adoptium.net/temurin/releases/) in CI | Build one bridge per native host ABI. The end-user JVM remains a runtime requirement. |
| Clang, LLVM backends, LLD, compiler-rt | Pinned release/source from [llvm/llvm-project](https://github.com/llvm/llvm-project) | Build a host-native tool bundle with X86, AArch64, and RISCV backends. Use `lld-link` for MSVC-style COFF links. |
| Linux C runtime | Pinned source from [musl](https://git.musl-libc.org/cgit/musl/) | Build headers, startup objects, libc, and linker metadata for x86_64, AArch64, and RISC-V 64. Record the RISC-V ISA/ABI baseline in the target profile. |
| Windows GNU fallback | [LLVM-MinGW](https://github.com/mstorsjo/llvm-mingw) or pinned `mingw-w64` sources | Use only if the MSVC bundle fails its redistribution review. Select UCRT explicitly. |
| Windows MSVC sysroot | Windows SDK and Visual Studio Build Tools components from Microsoft's official distribution channels | A Windows runner can provision the files for CI. Cross-host use still requires the sysroot in each package. Redistribution is a release gate, not assumed permission. |
| macOS SDK | Xcode image on a macOS GitHub runner, for CI-only validation | Do not include it in release artifacts unless Apple licensing permits that distribution. A custom public-API subset would be a separately scoped target profile. |

## Host build requirements

| Host profile | GitHub Actions runner | Host-specific build inputs |
|---|---|---|
| `linux-x86_64` | `ubuntu-24.04` | JDK; Linux-native FFI bridge built for glibc and musl; static-musl host tools. |
| `linux-aarch64` | `ubuntu-24.04-arm` | JDK; AArch64 FFI bridge built for glibc and musl; static-musl host tools. |
| `linux-riscv64` | No hosted RISC-V runner currently listed | Cross-build the host tools and bridge from an x64/ARM64 Linux runner with a pinned RISC-V sysroot; execute smoke tests under QEMU or on a self-hosted RISC-V runner. |
| `macos-aarch64` | `macos-15` (ARM64) | JDK; native Mach-O FFI bridge and host tools. Xcode is a CI input only unless separately licensed for redistribution. |
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

