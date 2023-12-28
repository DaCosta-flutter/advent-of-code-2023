import utils.geometry.Position
import utils.geometry.cartesianNeighbours
import utils.geometry.atPos
import utils.geometry.down
import utils.geometry.left
import utils.println
import utils.readInput
import utils.geometry.right
import utils.geometry.toGrid
import utils.geometry.up

fun main() {
    val day = "23"


    fun part1(input: List<String>): Long {
        val trailByPosition = input.toGrid()
        val startPosition = input[0].indices.find { xPos -> input[0][xPos] != '#' }.let { Position(it!!, 0) }
        val endPosition = input.last().indices.find { xPos -> input.last()[xPos] != '#' }.let { Position(it!!, input.lastIndex) }

        fun longestDistance(curPos: Position, visited: Set<Position> = emptySet()): Int {
            if (curPos == endPosition) {
                return 0
            }
            val toVisit = when (input.atPos(curPos)) {
                '.' -> curPos.cartesianNeighbours().filter { it in trailByPosition && trailByPosition[it] != '#' }
                '>' -> setOf(curPos.right())
                '<' -> setOf(curPos.left())
                '^' -> setOf(curPos.up())
                'v' -> setOf(curPos.down())
                else -> throw IllegalArgumentException("Unexpected char")
            }.filter { it !in visited }
            if (toVisit.isEmpty()) {
                return 1
            } else {
                return 1 + toVisit.maxOf { longestDistance(it, visited + curPos) }
            }
        }

        return longestDistance(startPosition).toLong()
    }

    fun part2(input: List<String>): Long {
        val trailByPosition = input.toGrid()
        val startPosition = input[0].indices.find { xPos -> input[0][xPos] != '#' }.let { Position(it!!, 0) }
        val endPosition = input.last().indices.find { xPos -> input.last()[xPos] != '#' }.let { Position(it!!, input.lastIndex) }

        data class Vertex(
            val pos: Position,
            val connectedVertices: MutableMap<Position, Int> = mutableMapOf(),
            val positionsBetweenVertices: MutableMap<Position, Set<Position>> = mutableMapOf()
        )

        val vertexByPos = mutableMapOf<Position, Vertex>()
        val visited = mutableSetOf<Position>()
        fun buildGraphFrom(
            pos: Position,
            previousVertex: Vertex,
            distanceToPreviousVertex: Int,
            positionsBetweenPreviousVertex: Set<Position>
        ): Pair<Vertex, Int>? {
            visited.add(pos)
            if (pos == endPosition) {
                val finalVertex = vertexByPos.computeIfAbsent(pos, { Vertex(pos) })
                finalVertex.connectedVertices[previousVertex.pos] = distanceToPreviousVertex
                finalVertex.positionsBetweenVertices[previousVertex.pos] = positionsBetweenPreviousVertex

                return finalVertex to distanceToPreviousVertex
            }
            val toVisit = pos.cartesianNeighbours()
                .filter { it in trailByPosition }
                .filter { trailByPosition[it] != '#' }
                .filter { it !in visited }
                .toSet()

            if (toVisit.isEmpty()) {
                pos.cartesianNeighbours().filter { it in vertexByPos }
                    .filter { it != previousVertex.pos }
                    .map { vertexByPos[it]!! }
                    .map { vertex ->
                        vertex.connectedVertices[previousVertex.pos] = distanceToPreviousVertex + 1
                        vertex.positionsBetweenVertices[previousVertex.pos] = positionsBetweenPreviousVertex + vertex.pos
                    }
                return null
            }
            if (toVisit.size == 1) {
                return buildGraphFrom(toVisit.first(), previousVertex, distanceToPreviousVertex + 1, positionsBetweenPreviousVertex + pos)
            } else {
                val vertex = Vertex(pos)
                vertexByPos[pos] = vertex
                // Is a vertex
                toVisit
                    .mapNotNull { buildGraphFrom(it, vertex, 1, emptySet()) }
                    .toSet()

                vertex.connectedVertices[previousVertex.pos] = distanceToPreviousVertex
                vertex.positionsBetweenVertices[previousVertex.pos] = positionsBetweenPreviousVertex

                /*connectedTo.forEach { (vertex, distance) ->
                    vertex.connectedVertices[vertex.pos] = distance
                }*/
                return vertex to distanceToPreviousVertex
            }
        }

        val startVertex = Vertex(startPosition)
        vertexByPos[startPosition] = startVertex
        buildGraphFrom(startPosition, startVertex, 0, emptySet())

        // Adust pairwise connections
        vertexByPos.values.forEach { curVertex ->
            curVertex.connectedVertices.forEach { (vertexPos, distance) ->
                vertexByPos[vertexPos]!!.connectedVertices[curVertex.pos] = distance
            }
        }

        val connectingVertices = vertexByPos.values.flatMap { it.positionsBetweenVertices.values }.flatten().toSet()

        fun printVertices() {
            input.indices.map { y ->
                input[y].indices.map { x ->
                    val currentPos = Position(x, y)
                    if (currentPos in vertexByPos) print("*")
                    else if (currentPos in connectingVertices) print(".")
                    else if (input.atPos(currentPos) == '#') print("#")
                    else print(" ")
                }
                println()
            }
        }
        printVertices()

        fun longestDistanceWithoutSlopes(curPos: Position, visited: Set<Position> = emptySet(), distanceFromLastVertex: Int = 0): Int? {
            if (curPos == endPosition) {
                return distanceFromLastVertex
            }
            val toVisit = vertexByPos[curPos]!!.connectedVertices
                .filter { it.key !in visited }

            return toVisit
                .mapNotNull { (pos, distance) -> longestDistanceWithoutSlopes(pos, visited + curPos, distance) }
                .maxOrNull()
                ?.let { it + distanceFromLastVertex }
        }

        return longestDistanceWithoutSlopes(startPosition)!!.toLong()
    }

// test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${day}_test")

// Check test inputs
    utils.check(94L, part1(testInput), "Part 1")
    utils.check(154, part2(testInput), "Part 2")

    val input = readInput("Day${day}")
    part1(input).println()
    part2(input).println()
}