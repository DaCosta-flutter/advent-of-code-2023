package y2023

import utils.println
import utils.readInput2023

fun main() {
    val day = "09"

    data class Sequence(
        val values: List<Int>
    )

    fun parseInput(input: List<String>): List<Sequence> {
        return input.map { line ->
            Sequence(line.split(" ").map { it.toInt() })
        }
    }

    fun Sequence.diffed(): Sequence = this.values.windowed(2).map {
        it[1] - it[0]
    }.let {
        Sequence(it)
    }

    fun Sequence.next(): Int {
        if (this.values.all { it == 0 }) return 0
        return this.values.last() + this.diffed().next()
    }

    fun Sequence.previous(): Int {
        if (this.values.all { it == 0 }) return 0
        return this.values.first() - this.diffed().previous()
    }

    fun part1(input: List<String>): Long {
        val sequences = parseInput(input)

        return sequences.sumOf { it.next() }.toLong()
    }

    fun part2(input: List<String>): Long {
        val sequences = parseInput(input)

        return sequences.sumOf { it.previous() }.toLong()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput2023("Day${day}_test")

    // Check test inputs
    utils.check(114L, part1(testInput), "Part 1")
    utils.check(2L, part2(testInput), "Part 2")

    val input = readInput2023("Day${day}")
    part1(input).println()
    part2(input).println()
}