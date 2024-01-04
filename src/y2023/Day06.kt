package y2023

import utils.println
import utils.readInput2023

fun main() {
    val day = "06"

    data class RaceRecord(
        val time: Long,
        val distance: Long,
    )

    fun parseInputPart1(input: List<String>): List<RaceRecord> {
        val times = input[0].removePrefix("Time:").split(" ")
            .filter { it.isNotBlank() }
            .map { it.trim().toLong() }
        val distances = input[1].removePrefix("Distance:").trim().split(" ")
            .filter { it.isNotBlank() }
            .map { it.trim().toLong() }

        return times.indices
            .map { RaceRecord(times[it], distances[it]) }
    }

    fun parseInputPart2(input: List<String>): RaceRecord {
        val time = input[0].removePrefix("Time:").filter { it != ' ' }.toLong()
        val distance = input[1].removePrefix("Distance:").filter { it != ' ' }.toLong()

        return RaceRecord(time, distance)
    }

    fun RaceRecord.calcNumPossibleRecordBreakingRaceDistances(): Int {
        return (1..this.time)
            .map { buttonTime ->
                buttonTime * (this.time - buttonTime)
            }.count { it > this.distance }
    }

    fun part1(input: List<String>): Long {
        val raceRecords = parseInputPart1(input)

        return raceRecords
            .map { it.calcNumPossibleRecordBreakingRaceDistances() }
            .fold(1L) { acc, l -> acc * l }
    }

    fun part2(input: List<String>): Long {
        val raceRecord = parseInputPart2(input)

        return raceRecord.calcNumPossibleRecordBreakingRaceDistances().toLong()
    }

// test if implementation meets criteria from the description, like:
    val testInput = readInput2023("Day${day}_test")
    val testResult = part2(testInput)

    println("Test input result: $testResult")
    check(testResult == 71503L)

    val input = readInput2023("Day${day}")
    part1(input).println()
    part2(input).println()
}

