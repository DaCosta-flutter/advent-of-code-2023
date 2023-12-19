import java.math.BigDecimal
import java.math.BigInteger
import kotlin.math.abs

enum class Direction2D {
    UP, DOWN, RIGHT, LEFT
}

typealias Instruction = Pair<Direction2D, Int>

fun main() {
    val day = "18"

    fun Position.move(num: Int, d: Direction2D): List<Position> {
        val positions = mutableListOf<Position>()
        var currentPos = this
        repeat(num) {
            currentPos = when (d) {
                Direction2D.UP -> currentPos.up()
                Direction2D.DOWN -> currentPos.down()
                Direction2D.RIGHT -> currentPos.right()
                Direction2D.LEFT -> currentPos.left()
            }
            positions.add(currentPos)
        }
        return positions
    }

    fun Position.nextPosition(num: Int, d: Direction2D) = when (d) {
        Direction2D.UP -> this.copy(y = y - num)
        Direction2D.DOWN -> this.copy(y = y + num)
        Direction2D.RIGHT -> this.copy(x = x + num)
        Direction2D.LEFT -> this.copy(x = x - num)
    }

    fun areaOfPolygon(vertices: List<Position>): Long {
        var n = vertices.size - 1
        var area = BigInteger.ZERO

        for (i in 0 until n) {
            area = area.add(
                vertices[n].x.toBigInteger().add(vertices[i].x.toBigInteger())
                    .multiply(vertices[n].y.toBigInteger().subtract(vertices[i].y.toBigInteger()))
            );
            //area += (vertices[n].x + vertices[i].x) * (vertices[n].y - vertices[i].y)
            n = i
        }

        return area.abs().divide(2.toBigInteger()).toLong()
    }

    fun calcArea(instructions: List<Instruction>): Long {
        val vertices = mutableListOf<Position>().apply { add(Position(0, 0)) }
        var numEdges = 0L
        instructions.forEach { (direction, numMoves) ->
            val newVertex = vertices.last().nextPosition(numMoves, direction)
            numEdges += newVertex.cartesianDistance(vertices.last())
            vertices.add(newVertex)
        }

        val interiorPoints = areaOfPolygon(vertices) - numEdges / 2 + 1
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
    val testInput = readInput("Day${day}_test")

    // Check test inputs
    check(62L, part1(testInput), "Part 1")
    check(952408144115L, part2(testInput), "Part 2")

    val input = readInput("Day${day}")
    part1(input).println()
    part2(input).println()
}