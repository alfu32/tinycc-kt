package org.tinycc.api

import java.nio.file.Path

enum class CompilationStatus {
    SUCCEEDED,
    FAILED,
    INVALID_REQUEST,
    TOOLCHAIN_UNAVAILABLE
}

enum class DiagnosticSeverity {
    ERROR,
    WARNING,
    NOTE
}

data class CompilationDiagnostic @JvmOverloads constructor(
    val severity: DiagnosticSeverity,
    val message: String,
    val file: Path? = null,
    val line: Int? = null,
    val column: Int? = null
)

data class CompilationResult @JvmOverloads constructor(
    val status: CompilationStatus,
    val exitCode: Int,
    val output: Path? = null,
    val diagnostics: List<CompilationDiagnostic> = emptyList(),
    val standardOutput: String = "",
    val standardError: String = "",
    val durationMillis: Long = 0
) {
    val succeeded: Boolean
        get() = status == CompilationStatus.SUCCEEDED && exitCode == 0
}
