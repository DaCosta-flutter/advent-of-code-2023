package y2023

import utils.println
import utils.readInput2023

fun main() {
    fun part1(input: List<String>): Int {
        return input
            .map { it.replace("\\D".toRegex(), "") }
            .map { "${it.first()}${it.last()}" }
            .sumOf { it.toInt() }
    }

    fun part2(input: List<String>): Int {
        println(input)
        return input
            .map { it.replace("one", "one1one") }
            .map { it.replace("two", "two2two") }
            .map { it.replace("three", "three3three") }
            .map { it.replace("four", "four4four") }
            .map { it.replace("five", "five5five") }
            .map { it.replace("six", "six6six") }
            .map { it.replace("seven", "seven7seven") }
            .map { it.replace("eight", "eight8eight") }
            .map { it.replace("nine", "nine9") }
            .let { part1(it) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput2023("Day01_test")
    val part2Result = part2(testInput)

    println("Test input part 2: $part2Result")
    check(part2(testInput) == 281)

    val input = readInput2023("Day01")
    part1(input).println()
    part2(input).println()
}
