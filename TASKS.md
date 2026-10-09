# Project task register

## Status report

- **DONE:** 14
- **DOING:** 0
- **TODO:** 26
- **Last updated:** 2026-10-09T17:26:44Z
- **Counting rule:** counts include every task and subtask record, including parent workstreams.

## Tasks

### T000 — Establish task tracking

- **Status:** DONE
- **begin_datetime:** 2026-10-09T17:04:13Z
- **resolution_datetime:** 2026-10-09T17:05:03Z
- **Notes:** Added the task-register requirements to `AGENTS.md` and created the initial breakdown and status report.

### T001 — Specify the initial cross-build packaging matrix

- **Status:** DONE
- **begin_datetime:** null
- **resolution_datetime:** 2026-10-09T16:57:45Z
- **Notes:** Completed before task tracking was initialized; the start time was not recorded. Specification is in `scripts/README.md` and `scripts/matrix.toml`.

### T002 — Initialize agent and response conventions

- **Status:** DONE
- **begin_datetime:** null
- **resolution_datetime:** 2026-10-09T17:00:14Z
- **Notes:** Completed before task tracking was initialized; the start time was not recorded. Rules are in `AGENTS.md`.

### T010 — Finalize host and target support contracts

- **Status:** DONE
- **begin_datetime:** null
- **resolution_datetime:** 2026-10-09T17:16:40Z
- **Notes:** Work began before task tracking; its exact start time is unavailable. Defined Linux musl targets, Windows GNU/UCRT targets with deferred MSVC profiles, the macOS SDK boundary, and Linux libc-specific FFI bridge variants.

#### T010.1 — Define the initial Linux musl target profiles

- **Status:** DONE
- **begin_datetime:** null
- **resolution_datetime:** 2026-10-09T16:57:45Z
- **Notes:** Completion predates task tracking; start time was not recorded. Profiles cover x86_64, AArch64, and RISC-V 64; the RISC-V ISA/ABI baseline still needs a pinned value under T040.3.

#### T010.2 — Select a distributable Windows runtime profile

- **Status:** DONE
- **begin_datetime:** 2026-10-09T17:11:35Z
- **resolution_datetime:** 2026-10-09T17:13:12Z
- **Notes:** Selected LLVM-MinGW with UCRT for GNU Windows x86_64/AArch64, targeting Windows 10 and later. MSVC profiles are deferred until a file-by-file redistribution review clears a self-contained sysroot.

#### T010.3 — Define out-of-box macOS target support

- **Status:** DONE
- **begin_datetime:** 2026-10-09T17:14:27Z
- **resolution_datetime:** 2026-10-09T17:14:49Z
- **Notes:** Defined Darwin support as requiring a locally installed Apple SDK. Command Line Tools avoid requiring the full Xcode app, but include the same macOS SDK and must still be installed. Apple's agreement blocks bundling the SDK or using it on non-Apple hosts; matrix narrowed to 31 legally valid pairs.

#### T010.4 — Finalize Linux host FFI ABI variants

- **Status:** DONE
- **begin_datetime:** 2026-10-09T17:15:35Z
- **resolution_datetime:** 2026-10-09T17:16:03Z
- **Notes:** Defined separate glibc and musl bridge variants for Linux hosts. The JVM loader detects the process libc through /proc/self/maps and supports the tinycc.native.libc system-property override; the package layout carries both variants.

### T020 — Establish the Kotlin/JVM application and API

- **Status:** DONE
- **begin_datetime:** 2026-10-09T17:17:53Z
- **resolution_datetime:** 2026-10-09T17:26:44Z
- **Notes:** Established the JVM 17 Gradle app, standalone JAR tasks, Kotlin request/result API, CLI contract, and FFI boundary.

#### T020.1 — Create the Gradle Kotlin DSL project and distribution task

- **Status:** DONE
- **begin_datetime:** 2026-10-09T17:17:53Z
- **resolution_datetime:** 2026-10-09T17:20:32Z
- **Notes:** Added the Gradle Kotlin DSL/JVM 17 project, wrapper, runnable fat JAR, and gated autonomousJar bundle task. compileKotlin and jar completed; launching the JAR with --version printed the expected version.

#### T020.2 — Define the compiler request/result API and CLI

- **Status:** DONE
- **begin_datetime:** 2026-10-09T17:21:20Z
- **resolution_datetime:** 2026-10-09T17:23:21Z
- **Notes:** Added Kotlin request/result models, target profiles, a CCompiler interface, and a parser for target selection and common C options. Help, version, target listing, and structured toolchain-unavailable results work; backend compilation remains for later tasks.

#### T020.3 — Define the stable C ABI and JVM bridge lifecycle

- **Status:** DONE
- **begin_datetime:** 2026-10-09T17:25:14Z
- **resolution_datetime:** 2026-10-09T17:26:44Z
- **Notes:** Defined tinycc_main(int argc, char *argv[]) as the exported C ABI, with UTF-8 arguments, exit-code/error behavior, Java/JAR discovery, and child-JVM lifecycle. Clarified that Windows DllMain is a separate loader callback.

### T030 — Build host-native compiler tool bundles

- **Status:** TODO
- **begin_datetime:** null
- **resolution_datetime:** null

#### T030.1 — Pin and build Clang, LLVM backends, LLD, and compiler-rt

- **Status:** TODO
- **begin_datetime:** null
- **resolution_datetime:** null

#### T030.2 — Produce Linux x86_64 and AArch64 host payloads

- **Status:** TODO
- **begin_datetime:** null
- **resolution_datetime:** null

#### T030.3 — Produce Windows and macOS host payloads

- **Status:** TODO
- **begin_datetime:** null
- **resolution_datetime:** null

#### T030.4 — Cross-build and qualify the Linux RISC-V host payload

- **Status:** TODO
- **begin_datetime:** null
- **resolution_datetime:** null
- **Notes:** GitHub-hosted runners do not currently include RISC-V; use cross-building plus emulation or a self-hosted runner.

### T040 — Build target sysroots and profiles

- **Status:** TODO
- **begin_datetime:** null
- **resolution_datetime:** null

#### T040.1 — Build x86_64 and AArch64 Linux musl sysroots

- **Status:** TODO
- **begin_datetime:** null
- **resolution_datetime:** null

#### T040.2 — Build the Windows MSVC sysroots or MinGW-w64 fallback

- **Status:** TODO
- **begin_datetime:** null
- **resolution_datetime:** null
- **Notes:** Depends on the distribution decision in T010.2.

#### T040.3 — Pin the RISC-V Linux ISA/ABI and build its musl sysroot

- **Status:** TODO
- **begin_datetime:** null
- **resolution_datetime:** null

#### T040.4 — Build a supported macOS sysroot/profile

- **Status:** TODO
- **begin_datetime:** null
- **resolution_datetime:** null
- **Notes:** Depends on T010.3 and the SDK distribution boundary.

### T050 — Implement per-pair provisioning and packaging scripts

- **Status:** TODO
- **begin_datetime:** null
- **resolution_datetime:** null

#### T050.1 — Implement shared Python provisioning/build helpers

- **Status:** TODO
- **begin_datetime:** null
- **resolution_datetime:** null

#### T050.2 — Add one thin build entry point for every supported host-target pair

- **Status:** TODO
- **begin_datetime:** null
- **resolution_datetime:** null

#### T050.3 — Emit the common package layout and checksummed manifest

- **Status:** TODO
- **begin_datetime:** null
- **resolution_datetime:** null

### T060 — Add manually dispatched GitHub release workflows

- **Status:** TODO
- **begin_datetime:** null
- **resolution_datetime:** null

#### T060.1 — Add one manual workflow for each supported host-target pair

- **Status:** TODO
- **begin_datetime:** null
- **resolution_datetime:** null

#### T060.2 — Require and check out the selected release tag

- **Status:** TODO
- **begin_datetime:** null
- **resolution_datetime:** null

#### T060.3 — Append uniquely named pair artifacts to the same tag release

- **Status:** TODO
- **begin_datetime:** null
- **resolution_datetime:** null
- **Notes:** Serialize uploads per tag and make reruns replace only the matching asset.

### T070 — Implement FFI host routing and offline distribution

- **Status:** TODO
- **begin_datetime:** null
- **resolution_datetime:** null

#### T070.1 — Route callers by process OS, architecture, and Linux libc

- **Status:** TODO
- **begin_datetime:** null
- **resolution_datetime:** null

#### T070.2 — Support separate host and target package installation

- **Status:** TODO
- **begin_datetime:** null
- **resolution_datetime:** null

#### T070.3 — Assemble the all-in-one offline distribution

- **Status:** TODO
- **begin_datetime:** null
- **resolution_datetime:** null

### T080 — Qualify the host-target matrix

- **Status:** TODO
- **begin_datetime:** null
- **resolution_datetime:** null

#### T080.1 — Compile and link a C smoke program for every supported pair

- **Status:** TODO
- **begin_datetime:** null
- **resolution_datetime:** null

#### T080.2 — Execute native outputs and emulate or inspect cross-target outputs

- **Status:** TODO
- **begin_datetime:** null
- **resolution_datetime:** null

#### T080.3 — Verify artifact layout, runtime dependencies, and licenses

- **Status:** TODO
- **begin_datetime:** null
- **resolution_datetime:** null

### T090 — Add repository ignore rules

- **Status:** DONE
- **begin_datetime:** 2026-10-09T17:06:55Z
- **resolution_datetime:** 2026-10-09T17:07:02Z
- **Notes:** Ignores Gradle/Kotlin and IDE output, Python script caches, and generated build packages.

### T091 — Commit each completed task

- **Status:** DONE
- **begin_datetime:** 2026-10-09T17:09:56Z
- **resolution_datetime:** 2026-10-09T17:10:01Z
- **Notes:** Add the per-task commit requirement to the agent management rules.
