import org.gradle.api.file.DuplicatesStrategy
import org.gradle.jvm.tasks.Jar

plugins {
    kotlin("jvm") version "2.4.20"
    application
}

group = "org.tinycc"
version = providers.gradleProperty("tinyccVersion").getOrElse("0.1.0-SNAPSHOT")

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("org.tinycc.cli.MainKt")
}

tasks.named<Jar>("jar") {
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
        attributes["Implementation-Version"] = project.version.toString()
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
    from({
        configurations.runtimeClasspath.get().map { entry ->
            if (entry.isDirectory) entry else zipTree(entry)
        }
    })
}

val autonomousBundleDirectory = layout.buildDirectory.dir("autonomous-bundle")

tasks.register<Jar>("autonomousJar") {
    group = "distribution"
    description = "Build a runnable fat JAR containing the staged native tools and target sysroots."
    dependsOn(tasks.named("classes"))
    inputs.dir(autonomousBundleDirectory)

    archiveClassifier.set("autonomous")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
        attributes["Implementation-Version"] = project.version.toString()
    }
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true

    from(sourceSets.main.get().output)
    from({
        configurations.runtimeClasspath.get().map { entry ->
            if (entry.isDirectory) entry else zipTree(entry)
        }
    })
    from(autonomousBundleDirectory) {
        into("META-INF/tinycc")
    }

    doFirst {
        val manifest = autonomousBundleDirectory.get().file("manifest.json").asFile
        if (!manifest.isFile) {
            throw GradleException(
                "The autonomous toolchain bundle is missing at ${autonomousBundleDirectory.get().asFile}. " +
                    "Build and stage the host/target packages before running autonomousJar."
            )
        }
    }
}
