import utils.println
import utils.readInput2023

fun main() {
    val day = "CHANGE_ME"

    fun part1(input: List<String>): Long {
        return input.size.toLong()
    }

    fun part2(input: List<String>): Long {
        return input.size.toLong()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput2023("Day${day}_test")

    // Check test inputs
    utils.check(testInput.size.toLong(), part1(testInput), "Part 1")
    utils.check(testInput.size.toLong(), part2(testInput), "Part 2")

    val input = readInput2023("Day${day}")
    part1(input).println()
    part2(input).println()
}