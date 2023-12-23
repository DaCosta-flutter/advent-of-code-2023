fun main() {
    val day = "23"


    fun part1(input: List<String>): Long {
        val trailByPosition = input.toCharsByPosition()
        val startPosition = input[0].indices.find { xPos -> input[0][xPos] != '#' }.let { Position(it!!, 0) }
        val endPosition = input.last().indices.find { xPos -> input.last()[xPos] != '#' }.let { Position(it!!, input.lastIndex) }

        fun longestDistance(curPos: Position, visited: Set<Position> = emptySet()): Int {
            if (curPos == endPosition) {
                return 0
            }
            val toVisit = when (input.atPos(curPos)) {
                '.' -> curPos.allDirections().filter { it in trailByPosition && trailByPosition[it] != '#' }
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
        val trailByPosition = input.toCharsByPosition()
        val startPosition = input[0].indices.find { xPos -> input[0][xPos] != '#' }.let { Position(it!!, 0) }
        val endPosition = input.last().indices.find { xPos -> input.last()[xPos] != '#' }.let { Position(it!!, input.lastIndex) }

        data class Vertex(
            val pos: Position,
            val connectedVertices: MutableMap<Position, Int> = mutableMapOf()
        )

        val vertexByPos = mutableMapOf<Position, Vertex>()
        val visited = mutableSetOf<Position>()
        fun buildGraphFrom(pos: Position, previousVertex: Vertex, distanceToPreviousVertex: Int): Pair<Vertex, Int>? {
            visited.add(pos)
            if (pos == endPosition) {
                val finalVertex = vertexByPos.computeIfAbsent(pos, { Vertex(pos) })
                finalVertex.connectedVertices.put(previousVertex.pos, distanceToPreviousVertex)

                return finalVertex to distanceToPreviousVertex
            }
            val toVisit = pos.allDirections()
                .filter { it in trailByPosition }
                .filter { trailByPosition[it] != '#' }
                .filter { it !in visited }
                .toSet()

            if (toVisit.isEmpty()) {
                return null
            }
            if (toVisit.size == 1) {
                return buildGraphFrom(toVisit.first(), previousVertex, distanceToPreviousVertex + 1)
            } else {
                val vertex = Vertex(pos)
                vertexByPos[pos] = vertex
                // Is a vertex
                val connectedTo = toVisit
                    .mapNotNull { buildGraphFrom(it, vertex, 1) }
                    .toSet()

                (connectedTo + (previousVertex to distanceToPreviousVertex))
                    .forEach {
                        vertex.connectedVertices.put(it.first.pos, it.second)
                    }
                return vertex to distanceToPreviousVertex
            }
        }

        val startVertex = Vertex(startPosition)
        vertexByPos[startPosition] = startVertex
        buildGraphFrom(startPosition, startVertex, 0)

        vertexByPos.values.forEach { curVertex ->
            curVertex.connectedVertices.forEach { (vertexPos, distance) ->
                vertexByPos[vertexPos]!!.connectedVertices[curVertex.pos] = distance
            }
        }

        fun printVertices() {
            input.indices.map { y ->
                input[y].indices.map { x ->
                    val currentPos = Position(x, y)
                    if (currentPos in vertexByPos) {
                        print("*")
                    } else print(input.atPos(currentPos))
                }
                println()
            }
        }
        printVertices()

        fun longestDistanceWithoutSlopes(curPos: Position, visited: Set<Position> = emptySet(), currentDistance: Int = 0): Int? {
            if (curPos == endPosition) {
                return 0
            }
            val toVisit = curPos.allDirections()
                .filter { it in trailByPosition }
                .filter { trailByPosition[it] != '#' }
                .filter { it !in visited }

            return toVisit
                .mapNotNull { longestDistanceWithoutSlopes(it, visited + curPos, currentDistance + 1) }
                .maxOrNull()
                ?.let { it + 1 }
        }

        return longestDistanceWithoutSlopes(startPosition)!!.toLong()
    }

// test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${day}_test")

// Check test inputs
    check(94L, part1(testInput), "Part 1")
    check(154, part2(testInput), "Part 2")

    val input = readInput("Day${day}")
    //part1(input).println()
    part2(input).println()
}