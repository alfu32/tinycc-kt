# tinycc-kt

tinycc-kt is a Kotlin/JVM front end and packaging shell for a self-contained C compiler distribution. The planned native compiler and target sysroots are specified in [scripts/README.md](scripts/README.md).

## Build

The Gradle build requires JDK 17 or newer. Use the checked-in Gradle wrapper:

    ./gradlew build

This creates the development application JAR under build/libs/. It does not contain a compiler toolchain yet.

## CLI and Kotlin API

List supported target profile IDs:

    java -jar build/libs/tinycc-kt-0.1.0-SNAPSHOT.jar --list-targets

The command-line driver accepts a target, standard C options, source files, and an output path:

    tinycc --target linux_x86_64_musl -std=c17 -O2 main.c -o app

The Kotlin API is in package org.tinycc.api. Callers construct a CompilationRequest, pass it to CCompiler.compile, and receive a CompilationResult containing status, exit code, output path, captured output, and structured diagnostics.

The CLI and API contracts are present, but compilation returns a toolchain-unavailable diagnostic until the native backend bundle is implemented.

The autonomousJar task creates the all-in-one runnable JAR after a complete toolchain bundle has been staged at build/autonomous-bundle/:

    ./gradlew autonomousJar

The staged directory must include its manifest.json, native host libraries, compiler executables, target sysroots, and license notices. Until those inputs exist, autonomousJar fails with a diagnostic instead of producing a misleadingly incomplete standalone package.
