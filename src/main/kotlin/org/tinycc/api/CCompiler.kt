package org.tinycc.api

/** Compiles C sources using a toolchain selected for the current host and requested target. */
fun interface CCompiler {
    fun compile(request: CompilationRequest): CompilationResult
}
