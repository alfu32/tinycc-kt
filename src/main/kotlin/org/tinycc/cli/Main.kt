package org.tinycc.cli

import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.isEmpty() || args[0] == "--help" || args[0] == "-h") {
        println(
            """
            tinycc — a self-contained cross-platform C compiler distribution

            Usage: tinycc <source.c> [compiler options]

            The compiler backend and target profiles are not bundled in this development build yet.
            """.trimIndent()
        )
        return
    }

    if (args[0] == "--version") {
        println("tinycc ${BuildInfo.version}")
        return
    }

    System.err.println("Compiler backend is not available in this development build.")
    exitProcess(2)
}

private object BuildInfo {
    const val version: String = "0.1.0-SNAPSHOT"
}
