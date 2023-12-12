import java.util.LinkedList
import kotlin.math.pow

enum class SpringStatus { DAMAGED, OPERATIONAL, UNKNOWN }

fun main() {
    val day = "12"

    data class Row(
        val springStatus: List<SpringStatus>,
        val damagedList: List<Int>,
    )

    data class Input(
        val rows: List<Row>
    )

    fun generatePermutations(num: Int): Set<List<SpringStatus>> = (0 until 2.0.pow(num).toInt())
        .map {
            String.format("%${num}s", Integer.toBinaryString(it)).replace(' ', '0').map { digit ->
                when (digit) {
                    '0' -> SpringStatus.DAMAGED
                    '1' -> SpringStatus.OPERATIONAL
                    else -> throw Exception("Unexpected digit $digit")
                }
            }
        }
        .toSet()

    fun Row.isValid(): Boolean {
        val damagedList = mutableListOf<Int>()
        var currentDamagedCount = 0
        this.springStatus.forEach {
            if (it == SpringStatus.DAMAGED) currentDamagedCount++
            else if (currentDamagedCount > 0) {
                damagedList.add(currentDamagedCount)
                currentDamagedCount = 0
            }
        }
        if (currentDamagedCount > 0) {
            damagedList.add(currentDamagedCount)
        }
        return damagedList == this.damagedList
    }

    fun Row.permutations(): List<Row> {
        val numUnknowns = this.springStatus.count { it == SpringStatus.UNKNOWN }
        val generatePermutations = generatePermutations(numUnknowns)
        return generatePermutations
            .map { permutation ->
                val toFetchUnknowns = LinkedList(permutation)
                this.springStatus.map {
                    if (it == SpringStatus.UNKNOWN) {
                        toFetchUnknowns.pop()
                    } else {
                        it
                    }
                }
            }.map { Row(it, this.damagedList) }
            .filter { it.isValid() }
            .toList()
    }

    fun parseInput(input: List<String>): Input {
        val res = input.map { line ->
            val (springsStr, damagedListStr) = line.split(" ").let { it[0] to it[1] }
            val springStatuses = springsStr.map {
                when (it) {
                    '?' -> SpringStatus.UNKNOWN
                    '.' -> SpringStatus.OPERATIONAL
                    '#' -> SpringStatus.DAMAGED
                    else -> throw IllegalArgumentException("$it status not valid")
                }
            }
            val damaged = damagedListStr.split(",").map { it.toInt() }
            Row(springStatuses, damaged)
        }
        return Input(res)
    }

    fun part1(input: List<String>): Long {
        val rows = parseInput(input)
        rows.rows[4].permutations()

        return rows.rows.sumOf { it.permutations().size }.toLong()
    }

    fun part2(input: List<String>): Long {
        return input.size.toLong()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${day}_test")

    // Check test inputs
    check(21L, part1(testInput), "Part 1")
    check(testInput.size.toLong(), part2(testInput), "Part 2")

    val input = readInput("Day${day}")
    part1(input).println()
    part2(input).println()
}