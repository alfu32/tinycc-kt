package org.tinycc.cli

import java.nio.file.Path
import org.tinycc.api.CStandard
import org.tinycc.api.CompilationRequest
import org.tinycc.api.OptimizationLevel
import org.tinycc.api.OutputKind
import org.tinycc.api.TargetProfile

internal sealed interface CliCommand {
    data object Help : CliCommand
    data object Version : CliCommand
    data object ListTargets : CliCommand
    data class Compile(val request: CompilationRequest) : CliCommand
}

internal class CommandLineException(message: String) : IllegalArgumentException(message)

internal object CommandLineParser {
    fun parse(args: Array<String>): CliCommand {
        if (args.isEmpty() || args.contentEquals(arrayOf("--help")) || args.contentEquals(arrayOf("-h"))) {
            return CliCommand.Help
        }
        if (args.contentEquals(arrayOf("--version"))) return CliCommand.Version
        if (args.contentEquals(arrayOf("--list-targets"))) return CliCommand.ListTargets

        var target: TargetProfile? = null
        var output: Path? = null
        var standard = CStandard.C17
        var optimization = OptimizationLevel.O0
        var outputKind = OutputKind.EXECUTABLE
        var sawCompileOnly = false
        var sawShared = false
        val sources = mutableListOf<Path>()
        val includes = mutableListOf<Path>()
        val systemIncludes = mutableListOf<Path>()
        val libraryDirectories = mutableListOf<Path>()
        val libraries = mutableListOf<String>()
        val definitions = linkedMapOf<String, String>()
        val undefinedMacros = linkedSetOf<String>()
        val extraArguments = mutableListOf<String>()

        fun takeValue(option: String, index: Int, inlineValue: String? = null): Pair<String, Int> {
            if (inlineValue != null) {
                if (inlineValue.isEmpty()) throw CommandLineException("Missing value for $option")
                return inlineValue to index
            }
            val nextIndex = index + 1
            if (nextIndex >= args.size || args[nextIndex].startsWith("-")) {
                throw CommandLineException("Missing value for $option")
            }
            return args[nextIndex] to nextIndex
        }

        fun addDefinition(raw: String) {
            val separator = raw.indexOf('=')
            val name = if (separator < 0) raw else raw.substring(0, separator)
            if (!name.matches(Regex("[A-Za-z_][A-Za-z0-9_]*"))) {
                throw CommandLineException("Invalid macro name: $name")
            }
            definitions[name] = if (separator < 0) "1" else raw.substring(separator + 1)
        }

        fun addUndefinition(name: String) {
            if (!name.matches(Regex("[A-Za-z_][A-Za-z0-9_]*"))) {
                throw CommandLineException("Invalid macro name: $name")
            }
            undefinedMacros += name
        }

        var index = 0
        var positionalOnly = false
        while (index < args.size) {
            val arg = args[index]
            if (positionalOnly) {
                sources.add(Path.of(arg))
                index++
                continue
            }
            when {
                arg == "--" -> positionalOnly = true
                arg == "--target" -> {
                    val (value, valueIndex) = takeValue(arg, index)
                    target = TargetProfile.fromId(value)
                        ?: throw CommandLineException("Unknown target profile: $value")
                    index = valueIndex
                }
                arg.startsWith("--target=") -> {
                    val value = arg.substringAfter('=')
                    target = TargetProfile.fromId(value)
                        ?: throw CommandLineException("Unknown target profile: $value")
                }
                arg == "-o" -> {
                    val (value, valueIndex) = takeValue(arg, index)
                    output = Path.of(value)
                    index = valueIndex
                }
                arg.startsWith("-o") && arg.length > 2 -> output = Path.of(arg.substring(2))
                arg == "-std" -> {
                    val (value, valueIndex) = takeValue(arg, index)
                    standard = CStandard.fromOption(value)
                        ?: throw CommandLineException("Unsupported C language standard: $value")
                    index = valueIndex
                }
                arg.startsWith("-std=") -> {
                    val value = arg.substringAfter('=')
                    standard = CStandard.fromOption(value)
                        ?: throw CommandLineException("Unsupported C language standard: $value")
                }
                arg == "-I" -> {
                    val (value, valueIndex) = takeValue(arg, index)
                    includes.add(Path.of(value))
                    index = valueIndex
                }
                arg.startsWith("-I") && arg.length > 2 -> includes.add(Path.of(arg.substring(2)))
                arg == "-isystem" -> {
                    val (value, valueIndex) = takeValue(arg, index)
                    systemIncludes.add(Path.of(value))
                    index = valueIndex
                }
                arg == "-L" -> {
                    val (value, valueIndex) = takeValue(arg, index)
                    libraryDirectories.add(Path.of(value))
                    index = valueIndex
                }
                arg.startsWith("-L") && arg.length > 2 -> libraryDirectories.add(Path.of(arg.substring(2)))
                arg == "-l" -> {
                    val (value, valueIndex) = takeValue(arg, index)
                    libraries += value
                    index = valueIndex
                }
                arg.startsWith("-l") && arg.length > 2 -> libraries += arg.substring(2)
                arg == "-D" -> {
                    val (value, valueIndex) = takeValue(arg, index)
                    addDefinition(value)
                    index = valueIndex
                }
                arg.startsWith("-D") && arg.length > 2 -> addDefinition(arg.substring(2))
                arg == "-U" -> {
                    val (value, valueIndex) = takeValue(arg, index)
                    addUndefinition(value)
                    index = valueIndex
                }
                arg.startsWith("-U") && arg.length > 2 -> addUndefinition(arg.substring(2))
                arg == "-O" -> optimization = OptimizationLevel.O1
                arg.startsWith("-O") -> optimization =
                    OptimizationLevel.fromOption(arg) ?: throw CommandLineException("Unsupported optimization level: $arg")
                arg == "-c" -> sawCompileOnly = true
                arg == "-shared" -> sawShared = true
                arg in setOf("-Xclang", "-Xlinker", "-Xassembler", "-mllvm", "-include", "-imacros") -> {
                    val (value, valueIndex) = takeValue(arg, index)
                    extraArguments += arg
                    extraArguments += value
                    index = valueIndex
                }
                arg.startsWith("-") -> extraArguments += arg
                else -> sources.add(Path.of(arg))
            }
            index++
        }

        if (sawCompileOnly && sawShared) {
            throw CommandLineException("-c and -shared cannot be used together")
        }
        if (sources.isEmpty()) throw CommandLineException("At least one C source file is required")
        val selectedTarget = target ?: throw CommandLineException("--target is required")
        outputKind = when {
            sawCompileOnly -> OutputKind.OBJECT
            sawShared -> OutputKind.SHARED_LIBRARY
            else -> outputKind
        }

        return CliCommand.Compile(
            CompilationRequest(
                sources = sources,
                target = selectedTarget,
                output = output,
                outputKind = outputKind,
                languageStandard = standard,
                optimization = optimization,
                includeDirectories = includes,
                systemIncludeDirectories = systemIncludes,
                libraryDirectories = libraryDirectories,
                libraries = libraries,
                definitions = definitions,
                undefinedMacros = undefinedMacros,
                extraArguments = extraArguments
            )
        )
    }
}
