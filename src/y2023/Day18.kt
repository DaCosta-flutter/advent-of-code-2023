package y2023

import utils.geometry.Direction2D
import utils.geometry.Grid
import utils.geometry.Point
import utils.geometry.manhattanDistance
import utils.geometry.moveTo
import utils.geometry.numInteriorPoints
import utils.println
import utils.readInput2023

typealias Instruction = Pair<Direction2D, Int>

fun main() {
    val day = "18"

    fun calcArea(instructions: List<Instruction>): Long {
        val vertices = mutableListOf<Point>().apply { add(Point(0, 0)) }
        var numEdges = 0L
        instructions.forEach { (direction, numMoves) ->
            val newVertex = vertices.last().moveTo(numMoves, direction)
            numEdges += newVertex.manhattanDistance(vertices.last())
            vertices.add(newVertex)
        }

        val verticesOfPolygon: Grid = vertices.associateWith { ' ' }
        val interiorPoints = verticesOfPolygon.numInteriorPoints(numEdges)
        return numEdges + interiorPoints
    }

    fun part1(input: List<String>): Long {
        val directionAndNumMoves = input.map { line ->
            val (directionChar, numMoves) = line.split(" ").let { it[0][0] to it[1].toInt() }
            val direction = when (directionChar) {
                'R' -> Direction2D.RIGHT
                'L' -> Direction2D.LEFT
                'D' -> Direction2D.DOWN
                'U' -> Direction2D.UP
                else -> throw IllegalStateException("$directionChar not a valid direction")
            }
            direction to numMoves
        }
        return calcArea(directionAndNumMoves)
    }

    fun part2(input: List<String>): Long {
        val instructions = input.map { line ->
            line.split(" ")[2].let {
                val direction = when (it[7]) {
                    '0' -> Direction2D.RIGHT
                    '1' -> Direction2D.DOWN
                    '2' -> Direction2D.LEFT
                    '3' -> Direction2D.UP
                    else -> throw IllegalArgumentException("${it[7]} is an invalid direction")
                }

                val numMoves = it.substring(2, 7).toInt(16)
                direction to numMoves
            }
        }

        return calcArea(instructions)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput2023("Day${day}_test")

    // Check test inputs
    utils.check(62L, part1(testInput), "Part 1")
    utils.check(952408144115L, part2(testInput), "Part 2")

    val input = readInput2023("Day${day}")
    part1(input).println()
    part2(input).println()
}