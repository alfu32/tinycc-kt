package org.tinycc.cli

import org.tinycc.api.CCompiler
import org.tinycc.api.CompilationDiagnostic
import org.tinycc.api.CompilationRequest
import org.tinycc.api.CompilationResult
import org.tinycc.api.CompilationStatus

internal object TinyCcCli {
    fun run(args: Array<String>, compiler: CCompiler = UnavailableCompiler): Int {
        val command = try {
            CommandLineParser.parse(args)
        } catch (error: CommandLineException) {
            System.err.println("tinycc: error: " + error.message)
            System.err.println("Run tinycc --help for usage.")
            return 2
        }

        return when (command) {
            CliCommand.Help -> {
                printUsage()
                0
            }
            CliCommand.Version -> {
                println("tinycc " + BuildInfo.version)
                0
            }
            CliCommand.ListTargets -> {
                org.tinycc.api.TargetProfile.entries.forEach { profile ->
                    val sdkNote = if (profile.requiresAppleSdk) " (requires local Apple SDK)" else ""
                    println(profile.id + "\t" + profile.llvmTriple + sdkNote)
                }
                0
            }
            is CliCommand.Compile -> runCompilation(command.request, compiler)
        }
    }

    private fun runCompilation(request: CompilationRequest, compiler: CCompiler): Int {
        val result = compiler.compile(request)
        printProcessOutput(result)
        result.diagnostics.forEach(::printDiagnostic)
        if (result.status == CompilationStatus.TOOLCHAIN_UNAVAILABLE && result.diagnostics.isEmpty()) {
            System.err.println("tinycc: error: native compiler toolchain is unavailable.")
        }
        return result.exitCode
    }

    private fun printProcessOutput(result: CompilationResult) {
        if (result.standardOutput.isNotEmpty()) print(result.standardOutput)
        if (result.standardError.isNotEmpty()) System.err.print(result.standardError)
    }

    private fun printDiagnostic(diagnostic: CompilationDiagnostic) {
        val location = buildList {
            diagnostic.file?.let { add(it.toString()) }
            diagnostic.line?.let { add(it.toString()) }
            diagnostic.column?.let { add(it.toString()) }
        }.joinToString(":")
        val prefix = if (location.isEmpty()) "tinycc" else location
        System.err.println(prefix + ": " + diagnostic.severity.name.lowercase() + ": " + diagnostic.message)
    }

    private fun printUsage() {
        println(
            """
            tinycc — a self-contained cross-platform C compiler distribution

            Usage:
              tinycc --target <profile> [options] <source.c>...
              tinycc --list-targets
              tinycc --version

            Options:
              -o <file>             Set output path
              -c                    Compile to object file(s), without linking
              -shared               Build a shared library
              -std=<name>           Select c89, c99, c11, c17, c23 or the matching gnu dialect
              -O0|-O1|-O2|-O3|-Os|-Oz
              -I <dir>              Add an include directory
              -isystem <dir>        Add a system include directory
              -D<name>[=<value>]    Define a preprocessor macro
              -U<name>              Undefine a preprocessor macro
              -L <dir>              Add a library search directory
              -l<name>              Link a library
              --                    Treat remaining arguments as source paths

            Target profile IDs are listed by --list-targets.
            """.trimIndent()
        )
    }
}

private object UnavailableCompiler : CCompiler {
    override fun compile(request: CompilationRequest): CompilationResult =
        CompilationResult(
            status = CompilationStatus.TOOLCHAIN_UNAVAILABLE,
            exitCode = 2,
            output = request.output,
            diagnostics = listOf(
                CompilationDiagnostic(
                    severity = org.tinycc.api.DiagnosticSeverity.ERROR,
                    message = "The native toolchain bundle is not installed in this development build."
                )
            )
        )
}

private object BuildInfo {
    val version: String = BuildMarker::class.java.getPackage()?.implementationVersion ?: "development"
}

private object BuildMarker
