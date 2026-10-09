package org.tinycc.api

import java.nio.file.Path

enum class CStandard(
    val clangOption: String,
    val gnuClangOption: String
) {
    C89("c89", "gnu89"),
    C99("c99", "gnu99"),
    C11("c11", "gnu11"),
    C17("c17", "gnu17"),
    C23("c23", "gnu23");

    companion object {
        @JvmStatic
        fun fromOption(option: String): CStandard? =
            entries.firstOrNull { option == it.clangOption || option == it.gnuClangOption }
    }
}

enum class OptimizationLevel(val option: String) {
    O0("-O0"),
    O1("-O1"),
    O2("-O2"),
    O3("-O3"),
    SIZE("-Os"),
    SIZE_MORE("-Oz");

    companion object {
        @JvmStatic
        fun fromOption(option: String): OptimizationLevel? =
            entries.firstOrNull { it.option == option }
    }
}

enum class OutputKind {
    EXECUTABLE,
    OBJECT,
    SHARED_LIBRARY
}

data class CompilationRequest @JvmOverloads constructor(
    val sources: List<Path>,
    val target: TargetProfile,
    val output: Path? = null,
    val outputKind: OutputKind = OutputKind.EXECUTABLE,
    val languageStandard: CStandard = CStandard.C17,
    val optimization: OptimizationLevel = OptimizationLevel.O0,
    val includeDirectories: List<Path> = emptyList(),
    val systemIncludeDirectories: List<Path> = emptyList(),
    val libraryDirectories: List<Path> = emptyList(),
    val libraries: List<String> = emptyList(),
    val definitions: Map<String, String> = emptyMap(),
    val undefinedMacros: Set<String> = emptySet(),
    val extraArguments: List<String> = emptyList()
)
