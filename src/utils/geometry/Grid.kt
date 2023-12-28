package utils.geometry

import java.math.BigInteger
import java.util.LinkedList
import java.util.PriorityQueue
import kotlin.math.min

typealias Grid = Map<Position, Char>

fun List<String>.toGrid(): Grid =
    this.indices.flatMap { y ->
        this[y].indices.map { x ->
            Position(x, y)
        }
    }.associateWith { this.atPos(it) }

fun Grid.cartesianNeighboursInGrid(pos: Position) = pos.cartesianNeighbours().filter { it in this }.toSet()

fun Grid.get2DArray(nonFilled: Char = ' '): MutableList<MutableList<Char>> {
    val minY = min(0, this.minOf { it.key.y })
    val minX = min(0, this.minOf { it.key.x })
    val maxY = this.maxOf { it.key.y }
    val maxX = this.maxOf { it.key.x }

    return (minY..maxY).map { y ->
        (minX..maxX).map { x ->
            this[Position(x, y)] ?: nonFilled
        }.toMutableList()
    }.toMutableList()
}

fun Grid.print(nonFilled: Char = ' ') {
    val arrayToPrint = this.get2DArray(nonFilled)
    arrayToPrint.forEach { line ->
        println()
        line.forEach { print(it) }
    }
    println()
}

fun Grid.bfs(
    startPosition: Position,
    inclusionCriteria: (Position) -> Boolean = { _ -> true },
    toVisitFromPos: (Position) -> Collection<Position> = { curPos -> this.cartesianNeighboursInGrid(curPos) }
): List<Position> {
    val toVisit = LinkedList<Position>().apply { add(startPosition) }
    val visited = mutableSetOf<Position>()

    while (toVisit.isNotEmpty()) {
        val curPos = toVisit.pop()
        toVisitFromPos(curPos)
            .filter { it !in visited }
            .filter(inclusionCriteria)
            .forEach {
                toVisit.add(it)
            }
    }
    return visited.toList()
}

/**
 * Calculate the area of a polygon, from its vertices coordinates - uses the Shoelace formula
 * https://en.wikipedia.org/wiki/Shoelace_formula
 * if charsToConsider is null, all positions in the grid will be considered
 */
fun Grid.areaOfPolygon(charsToConsider: Char? = null): Long {
    val vertices = this
        .filter { charsToConsider == null || it.value == charsToConsider }
        .keys.toList()

    var n = vertices.size - 1
    var area = BigInteger.ZERO

    for (i in 0 until n) {
        area = area.add(
            vertices[n].x.toBigInteger().add(vertices[i].x.toBigInteger())
                .multiply(vertices[n].y.toBigInteger().subtract(vertices[i].y.toBigInteger()))
        );
        n = i
    }

    return area.abs().divide(2.toBigInteger()).toLong()
}

/**
 * Uses Pick theorem
 * https://en.wikipedia.org/wiki/Pick%27s_theorem
 */
fun Grid.numInteriorPoints(numBoundaryPoints: Long, charsToConsider: Char? = null) =
    this.areaOfPolygon(charsToConsider) - numBoundaryPoints / 2 + 1

typealias Distance = Int

fun Grid.shortestPath(startPos: Position, targetPos: Position): Int? {
    val toVisit = PriorityQueue<Pair<Position, Distance>>(Comparator.comparing { it.second })
    val visited = mutableMapOf<Position, Distance>()

    while (toVisit.isNotEmpty() && targetPos !in visited) {
        val (curPos, distance) = toVisit.remove()
        this.cartesianNeighboursInGrid(curPos)
            .filter { it !in visited }
            .forEach {
                toVisit.add(it to distance + 1)
            }
    }

    return visited[targetPos]
}