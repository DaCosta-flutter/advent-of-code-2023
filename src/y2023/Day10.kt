package y2023

import utils.geometry.Point
import utils.println
import utils.readInput2023
import java.util.LinkedList

typealias PipeType = Char

fun main() {
    val day = "10"

    data class Tile(
        val point: Point,
        val pipeType: PipeType,
    )

    fun Tile.neighbours(): Set<Point> = when (pipeType) {
        '|' -> (-1..1 step 2).map { point.copy(y = point.y + it) }
        '-' -> (-1..1 step 2).map { point.copy(x = point.x + it) }
        'L' -> setOf(point.copy(y = point.y - 1), point.copy(x = point.x + 1))
        'J' -> setOf(point.copy(y = point.y - 1), point.copy(x = point.x - 1))
        '7' -> setOf(point.copy(y = point.y + 1), point.copy(x = point.x - 1))
        'F' -> setOf(point.copy(y = point.y + 1), point.copy(x = point.x + 1))
        'S' -> (-1..1).flatMap { x -> (-1..1).map { y -> x to y } }
            .map { point.copy(x = point.x + it.first, y = point.y + it.second) }.filterNot { it == point }

        else -> throw IllegalStateException("Invalid pipeType: '$pipeType'")
    }.toSet()

    fun Tile.getConnectedTo(tiles: Map<Point, Tile>) = this.neighbours()
        .mapNotNull { tiles[it] }
        .filter { this.point in it.neighbours() }.toSet()

    fun mainLoop(
        tiles: Map<Point, Tile>,
        startTile: Tile,
    ): Set<Tile> {
        val toVisit = LinkedList<Tile>().apply { add(startTile) }
        val visited = mutableSetOf<Tile>()

        while (toVisit.isNotEmpty()) {
            val tile = toVisit.pop()
            if (tile.pipeType == 'S' && visited.size > 2) {
                return visited.apply { add(Tile(tile.point, 'J')) }
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

    fun parseInput(input: List<String>): Map<Point, Tile> = input.indices
        .flatMap { y -> input[y].indices.map { x -> Point(x, y) } }
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
            .associateBy { it.point }

        return input.indices
            .flatMap { y ->
                var currentlyInside = false
                val insidePoints = mutableSetOf<Point>()
                input[y].indices.map { x ->
                    Point(x, y)
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
    val testInput = readInput2023("Day${day}_test")

    // Check test inputs
    //check(4L, part1(testInput), "Part 1")
    //check(4L, part2(testInput), "Part 2")

    val input = readInput2023("Day${day}")
    part1(input).println()
    part2(input).println()
}