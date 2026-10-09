package org.tinycc.cli

import kotlin.system.exitProcess

fun main(args: Array<String>) {
    exitProcess(TinyCcCli.run(args))
}
