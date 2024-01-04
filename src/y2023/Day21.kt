package y2023

import utils.check
import utils.geometry.Point
import utils.geometry.atPos
import utils.geometry.cardinalNeighbours
import utils.geometry.toGrid
import utils.println
import utils.readInput2023

fun main() {
    val day = "21"

    fun part1(input: List<String>): Long {
        val charByPosition = input.toGrid()

        val startPosition = charByPosition.filterValues { it == 'S' }.map { it.key }.first().also { it.println() }
        var curNumSteps = 0
        val validPosByNumSteps = mutableListOf(mutableSetOf(startPosition))

        while (curNumSteps <= 64) {
            validPosByNumSteps.add(mutableSetOf())
            validPosByNumSteps[curNumSteps].flatMap { it.cardinalNeighbours() }
                .filter { it in charByPosition && charByPosition[it] == '.' || charByPosition[it] == 'S' }
                .forEach { validPos ->
                    validPosByNumSteps[curNumSteps + 1].add(validPos)
                }
            curNumSteps++
        }

        /*(0..6)
            .forEach { println("Iter $it, numPositions: ${validPosByNumSteps[it].size} possible visited = ${validPosByNumSteps[it]}") }
*/
        return validPosByNumSteps[64].size.toLong()
    }

    fun printGrid(input: List<String>, possibleVisitedPoints: Set<Point>) {
        input.indices
            .asSequence()
            .flatMap { y ->
                input[y].indices.asSequence().map { x ->
                    Point(x, y)
                }
            }
            .forEach { curPos ->
                if (curPos.x == 0) println()
                if (curPos in possibleVisitedPoints) {
                    print(".")
                } else if (input.atPos(curPos) == '.') {
                    print(" ")
                } else {
                    print(input.atPos(curPos))
                }
            }
        println()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput2023("Day${day}_test")

    // Check test inputs
    check(42L, part1(testInput), "Part 1")

    val input = readInput2023("Day${day}")
    part1(input).println()
}