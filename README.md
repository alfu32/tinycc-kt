# tinycc-kt

tinycc-kt is a Kotlin/JVM front end and packaging shell for a self-contained C compiler distribution. The planned native compiler and target sysroots are specified in [scripts/README.md](scripts/README.md).

## Build

The Gradle build requires JDK 17 or newer. Use the checked-in Gradle wrapper:

    ./gradlew build

This creates the development application JAR under build/libs/. It does not contain a compiler toolchain yet.

The autonomousJar task creates the all-in-one runnable JAR after a complete toolchain bundle has been staged at build/autonomous-bundle/:

    ./gradlew autonomousJar

The staged directory must include its manifest.json, native host libraries, compiler executables, target sysroots, and license notices. Until those inputs exist, autonomousJar fails with a diagnostic instead of producing a misleadingly incomplete standalone package.
