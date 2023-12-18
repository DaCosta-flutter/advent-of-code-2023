import java.util.LinkedList

enum class Direction2D {
    UP, DOWN, RIGHT, LEFT
}

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

    fun printPositions(edgePositions: Set<Position>, insidePositions: Set<Position> = emptySet(), size: Position) {
        println()
        (0..size.y).forEach { y ->
            (0..size.x).forEach { x ->
                val position = Position(x, y)
                if (position in edgePositions)
                    print("#")
                else if (position in insidePositions)
                    print("O")
                else {
                    print(" ")
                }
            }
            println(" ")
        }
    }

    fun floodFill(startingPoint: Position, edges: Set<Position>): Set<Position> {
        val toVisit = LinkedList<Position>().apply { add(startingPoint) }
        val visited = HashSet<Position>()

        while (toVisit.isNotEmpty()) {
            val currentPosition = toVisit.pop()
            visited.add(currentPosition)
            currentPosition.allDirections()
                .forEach {
                    if (it !in toVisit && it !in visited && it !in edges) {
                        toVisit.add(it)
                    }
                }
        }

        return visited
    }

    fun part1(input: List<String>): Long {
        val edgePositions = mutableListOf<Position>().apply { add(Position(0, 0)) }
        input.forEach {
            val (directionChar, numMoves) = it.split(" ").let { it[0][0] to it[1].toInt() }
            val direction = when (directionChar) {
                'R' -> Direction2D.RIGHT
                'L' -> Direction2D.LEFT
                'D' -> Direction2D.DOWN
                'U' -> Direction2D.UP
                else -> throw IllegalStateException("$directionChar not a valid direction")
            }
            edgePositions.addAll(edgePositions.last().move(numMoves, direction))
        }

        val minX = edgePositions.minOf { it.x }.let { if (it < 0) it else 0 }
        val minY = edgePositions.minOf { it.y }.let { if (it < 0) it else 0 }

        val edgePositionsCorrected = edgePositions.map { Position(it.x - minX, it.y - minY) }.toSet()

        val size = Position(edgePositionsCorrected.maxOf { it.x }, edgePositionsCorrected.maxOf { it.y })

        //printPositions(edgePositionsCorrected, emptySet(), size)

        val insidePositions = floodFill(Position(35, 5), edgePositionsCorrected)
        printPositions(edgePositionsCorrected, insidePositions, size)

        return (insidePositions + edgePositionsCorrected).size.toLong()
    }

    fun part2(input: List<String>): Long {
        return input.size.toLong()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${day}_test")

    // Check test inputs
    //check(testInput.size.toLong(), part1(testInput), "Part 1")
    check(testInput.size.toLong(), part2(testInput), "Part 2")

    val input = readInput("Day${day}")
    part1(input).println()
    part2(input).println()
}