# Project task register

## Status report

- **DONE:** 6
- **DOING:** 1
- **TODO:** 33
- **Last updated:** 2026-10-09T17:10:01Z
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

- **Status:** DOING
- **begin_datetime:** null
- **resolution_datetime:** null
- **Notes:** Work began before task tracking; its exact start time is unavailable. Linux target profiles are recorded. Windows and macOS distribution constraints remain open.

#### T010.1 — Define the initial Linux musl target profiles

- **Status:** DONE
- **begin_datetime:** null
- **resolution_datetime:** 2026-10-09T16:57:45Z
- **Notes:** Completion predates task tracking; start time was not recorded. Profiles cover x86_64, AArch64, and RISC-V 64; the RISC-V ISA/ABI baseline still needs a pinned value under T040.3.

#### T010.2 — Select a distributable Windows runtime profile

- **Status:** TODO
- **begin_datetime:** null
- **resolution_datetime:** null
- **Notes:** Check the exact Microsoft SDK/VC files needed for MSVC ABI output and their redistribution terms; use MinGW-w64 + UCRT if bundling the MSVC files is not viable.

#### T010.3 — Define out-of-box macOS target support

- **Status:** TODO
- **begin_datetime:** null
- **resolution_datetime:** null
- **Notes:** Determine whether a project-owned limited sysroot can meet the required C profile without distributing Apple's SDK; otherwise define the supplied-SDK boundary.

#### T010.4 — Finalize Linux host FFI ABI variants

- **Status:** TODO
- **begin_datetime:** null
- **resolution_datetime:** null
- **Notes:** Decide how the loader selects glibc versus musl shared-library bridges for the host JVM process.

### T020 — Establish the Kotlin/JVM application and API

- **Status:** TODO
- **begin_datetime:** null
- **resolution_datetime:** null

#### T020.1 — Create the Gradle Kotlin DSL project and distribution task

- **Status:** TODO
- **begin_datetime:** null
- **resolution_datetime:** null

#### T020.2 — Define the compiler request/result API and CLI

- **Status:** TODO
- **begin_datetime:** null
- **resolution_datetime:** null

#### T020.3 — Define the stable C ABI and JVM bridge lifecycle

- **Status:** TODO
- **begin_datetime:** null
- **resolution_datetime:** null

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
