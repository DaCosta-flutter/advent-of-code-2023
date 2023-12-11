import java.util.LinkedList

typealias PipeType = Char

fun main() {
    val day = "10"

    data class Tile(
        val position: Position,
        val pipeType: PipeType,
    )

    fun Tile.neighbours(): Set<Position> = when (pipeType) {
        '|' -> (-1..1 step 2).map { position.copy(y = position.y + it) }
        '-' -> (-1..1 step 2).map { position.copy(x = position.x + it) }
        'L' -> setOf(position.copy(y = position.y - 1), position.copy(x = position.x + 1))
        'J' -> setOf(position.copy(y = position.y - 1), position.copy(x = position.x - 1))
        '7' -> setOf(position.copy(y = position.y + 1), position.copy(x = position.x - 1))
        'F' -> setOf(position.copy(y = position.y + 1), position.copy(x = position.x + 1))
        'S' -> (-1..1).flatMap { x -> (-1..1).map { y -> x to y } }
            .map { position.copy(x = position.x + it.first, y = position.y + it.second) }.filterNot { it == position }

        else -> throw IllegalStateException("Invalid pipeType: '$pipeType'")
    }.toSet()

    fun Tile.getConnectedTo(tiles: Map<Position, Tile>) = this.neighbours()
        .mapNotNull { tiles[it] }
        .filter { this.position in it.neighbours() }.toSet()

    fun mainLoop(
        tiles: Map<Position, Tile>,
        startTile: Tile,
    ): Set<Tile> {
        val toVisit = LinkedList<Tile>().apply { add(startTile) }
        val visited = mutableSetOf<Tile>()

        while (toVisit.isNotEmpty()) {
            val tile = toVisit.pop()
            if (tile.pipeType == 'S' && visited.size > 2) {
                return visited.apply { add(Tile(tile.position, 'J')) }
            } else if (tile.pipeType == 'S') {
                continue
            }
            visited.add(tile)
            tile.getConnectedTo(tiles)
                .filter { it !in visited || it.pipeType == 'S' }
                .forEach { toVisit.add(it) }

        }
        return emptySet()
    }

    fun parseInput(input: List<String>): Map<Position, Tile> = input.indices
        .flatMap { y -> input[y].indices.map { x -> Position(x, y) } }
        .filter { input[it.y][it.x] != '.' }
        .associateWith {
            Tile(it, input[it.y][it.x])
        }

    fun part1(input: List<String>): Long {
        val tiles = parseInput(input)

        val startTile = tiles.values.first { it.pipeType == 'S' }

        return startTile.getConnectedTo(tiles).maxOf { mainLoop(tiles, it).size / 2 }.toLong()
    }

    fun part2(input: List<String>): Long {
        val tiles = parseInput(input)

        val startTile = tiles.values.first { it.pipeType == 'S' }

        val mainLoopByPos = startTile.getConnectedTo(tiles).asSequence()
            .map { mainLoop(tiles, it) }
            .find { it.isNotEmpty() }!!
            .associateBy { it.position }

        return input.indices
            .flatMap { y ->
                var currentlyInside = false
                val insidePoints = mutableSetOf<Position>()
                input[y].indices.map { x ->
                    Position(x, y)
                }.forEach { currentPos ->
                    if (mainLoopByPos[currentPos]?.pipeType in setOf('|', '7', 'F')) {
                        currentlyInside = !currentlyInside
                    } else if (currentPos !in mainLoopByPos && currentlyInside) {
                        insidePoints.add(currentPos)
                    }
                }
                insidePoints
            }.count().toLong()

    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${day}_test")

    // Check test inputs
    //check(4L, part1(testInput), "Part 1")
    //check(4L, part2(testInput), "Part 2")

    val input = readInput("Day${day}")
    part1(input).println()
    part2(input).println()
}