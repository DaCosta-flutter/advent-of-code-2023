package y2023

import utils.geometry.Grid
import utils.geometry.Point
import utils.geometry.neighbours
import utils.geometry.toGrid
import utils.println
import utils.readInput2023

fun main() {
    val day = "03"

    fun Point.next() = this.copy(x = x + 1)

    fun getNumber(startPoint: Point, grid: Grid): Int {
        var isValid = false
        var currentVal = 0
        var numDigits = 0
        var currentPosition = startPoint

        while (currentPosition in grid && grid[currentPosition]!!.isDigit()) {
            currentVal = grid[currentPosition]!!.digitToInt() + currentVal * 10
            isValid = isValid || currentPosition.neighbours()
                .filter { it in grid }
                .any { adjPosition -> grid[adjPosition]!!.let { !it.isDigit() && it != '.' } }
            currentPosition = currentPosition.next()
            numDigits++
        }

        return if (isValid) currentVal else 0
    }

    fun parseNumbers(input: List<String>): List<Int> {
        var currentPos = Point(0, 0)
        val listOfNums = mutableListOf<Int>()
        val grid = input.toGrid()

        input.forEach {
            while (currentPos in grid) {
                val num = getNumber(currentPos, grid)
                if (num != 0) {
                    listOfNums.add(num)
                }
                repeat(num.toString().length) {
                    currentPos = currentPos.next()
                }
            }
            currentPos = currentPos.copy(x = 0, y = currentPos.y + 1)
        }
        return listOfNums
    }

    fun part1(input: List<String>): Int {
        return parseNumbers(input)
            .sum()
    }

    fun findGears(grid: Grid) = grid.filter { it.value == '*' }.map { it.key }

    fun findFromMiddleNumber(startPos: Point, grid: Grid): Int {
        var currentPos = startPos
        var prevPos = currentPos.copy(x = currentPos.x - 1)
        while (grid[prevPos]?.isDigit() == true) {
            currentPos = prevPos
            prevPos = currentPos.copy(x = currentPos.x - 1)
        }

        return getNumber(currentPos, grid)
    }

    fun findGearMultiplier(gearPoint: Point, grid: Grid): Int {
        val toMultiply = gearPoint.neighbours()
            .filter { grid[it]?.isDigit() ?: false }
            .map { findFromMiddleNumber(it, grid) }
            .toSet()

        return if (toMultiply.size <= 1) 0 else toMultiply.first() * toMultiply.last()

    }

    fun part2(input: List<String>): Int {
        val grid = input.toGrid()
        return findGears(grid).sumOf { findGearMultiplier(it, grid) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput2023("Day${day}_test")
    val testResult = part2(testInput)

    println("Test input: $testResult")
    check(testResult == 467835)

    val input = readInput2023("Day${day}")
    part1(input).println()
    part2(input).println()
}
