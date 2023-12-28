import utils.geometry.Point
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
        val startPoint = input[0].indices.find { xPos -> input[0][xPos] != '#' }.let { Point(it!!, 0) }
        val endPoint = input.last().indices.find { xPos -> input.last()[xPos] != '#' }.let { Point(it!!, input.lastIndex) }

        fun longestDistance(curPos: Point, visited: Set<Point> = emptySet()): Int {
            if (curPos == endPoint) {
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

        return longestDistance(startPoint).toLong()
    }

    fun part2(input: List<String>): Long {
        val trailByPosition = input.toGrid()
        val startPoint = input[0].indices.find { xPos -> input[0][xPos] != '#' }.let { Point(it!!, 0) }
        val endPoint = input.last().indices.find { xPos -> input.last()[xPos] != '#' }.let { Point(it!!, input.lastIndex) }

        data class Vertex(
            val pos: Point,
            val connectedVertices: MutableMap<Point, Int> = mutableMapOf(),
            val positionsBetweenVertices: MutableMap<Point, Set<Point>> = mutableMapOf()
        )

        val vertexByPos = mutableMapOf<Point, Vertex>()
        val visited = mutableSetOf<Point>()
        fun buildGraphFrom(
            pos: Point,
            previousVertex: Vertex,
            distanceToPreviousVertex: Int,
            positionsBetweenPreviousVertices: Set<Point>
        ): Pair<Vertex, Int>? {
            visited.add(pos)
            if (pos == endPoint) {
                val finalVertex = vertexByPos.computeIfAbsent(pos, { Vertex(pos) })
                finalVertex.connectedVertices[previousVertex.pos] = distanceToPreviousVertex
                finalVertex.positionsBetweenVertices[previousVertex.pos] = positionsBetweenPreviousVertices

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
                        vertex.positionsBetweenVertices[previousVertex.pos] = positionsBetweenPreviousVertices + vertex.pos
                    }
                return null
            }
            if (toVisit.size == 1) {
                return buildGraphFrom(toVisit.first(), previousVertex, distanceToPreviousVertex + 1, positionsBetweenPreviousVertices + pos)
            } else {
                val vertex = Vertex(pos)
                vertexByPos[pos] = vertex
                // Is a vertex
                toVisit
                    .mapNotNull { buildGraphFrom(it, vertex, 1, emptySet()) }
                    .toSet()

                vertex.connectedVertices[previousVertex.pos] = distanceToPreviousVertex
                vertex.positionsBetweenVertices[previousVertex.pos] = positionsBetweenPreviousVertices

                /*connectedTo.forEach { (vertex, distance) ->
                    vertex.connectedVertices[vertex.pos] = distance
                }*/
                return vertex to distanceToPreviousVertex
            }
        }

        val startVertex = Vertex(startPoint)
        vertexByPos[startPoint] = startVertex
        buildGraphFrom(startPoint, startVertex, 0, emptySet())

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
                    val currentPos = Point(x, y)
                    if (currentPos in vertexByPos) print("*")
                    else if (currentPos in connectingVertices) print(".")
                    else if (input.atPos(currentPos) == '#') print("#")
                    else print(" ")
                }
                println()
            }
        }
        printVertices()

        fun longestDistanceWithoutSlopes(curPos: Point, visited: Set<Point> = emptySet(), distanceFromLastVertex: Int = 0): Int? {
            if (curPos == endPoint) {
                return distanceFromLastVertex
            }
            val toVisit = vertexByPos[curPos]!!.connectedVertices
                .filter { it.key !in visited }

            return toVisit
                .mapNotNull { (pos, distance) -> longestDistanceWithoutSlopes(pos, visited + curPos, distance) }
                .maxOrNull()
                ?.let { it + distanceFromLastVertex }
        }

        return longestDistanceWithoutSlopes(startPoint)!!.toLong()
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