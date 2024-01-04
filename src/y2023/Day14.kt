package y2023

import utils.geometry.Point
import utils.geometry.toGrid
import utils.println
import utils.readInput2023

typealias PositionsByX = Map<Int, Set<Point>>

fun main() {
    val day = "14"

    fun parseInput(input: List<String>): Pair<PositionsByX, PositionsByX> {
        val grid = input.toGrid()
        val fixedRocks = grid.filter { it.value == '#' }.map { it.key }.groupBy { it.x }.mapValues { it.value.toSet() }
        val movableRocks = grid.filter { it.value == 'O' }.map { it.key }.groupBy { it.x }.mapValues { it.value.toSet() }

        return fixedRocks to movableRocks
    }

    val newMovableCache = mutableMapOf<Pair<PositionsByX, PositionsByX>, Pair<PositionsByX, String>>()

    fun computeNewMovableRockPositions(
        fixedRocksByX: PositionsByX,
        movableByX: PositionsByX,
        cycleNum: Int,
        orientation: String,
    ): PositionsByX {
        newMovableCache[fixedRocksByX to movableByX]?.let {
            println("Found repeated from cycle ${it.second}; current cycle is ${cycleNum} + $orientation")
            return it.first
        }

        val newMovableByX = mutableMapOf<Int, Set<Point>>()

        movableByX
            .forEach { (x, movablePositions) ->
                val newMovablePoints = mutableSetOf<Point>()
                val availableFixedByY = fixedRocksByX.getOrDefault(x, emptyList())

                movablePositions.sortedBy { it.y }
                    .forEach { movablePosition ->
                        val newPoint = (newMovablePoints + availableFixedByY)
                            .filter { it.x == movablePosition.x }
                            .filter { it.y < movablePosition.y }
                            .map { it.copy(y = it.y + 1) }
                            .maxByOrNull { it.y } ?: Point(movablePosition.x, 0)
                        newMovablePoints.add(newPoint)
                    }

                newMovableByX[x] = newMovablePoints
            }

        return newMovableByX.also {
            newMovableCache[fixedRocksByX to movableByX] = it to "$cycleNum $orientation"
        }
    }

    fun PositionsByX.rotateClockwise(newTotalLength: Point): PositionsByX {
        return this.values.flatMap { listOfPos ->
            listOfPos.map {
                Point(newTotalLength.x - 1 - it.y, it.x)
            }
        }.groupBy { it.x }.mapValues { it.value.toSet() }
    }

    fun part1(input: List<String>): Long {
        val (fixedRocks, movableRocks) = parseInput(input)

        val newPositionsMovableRocks = computeNewMovableRockPositions(fixedRocks, movableRocks, 0, "NORTH")

        return newPositionsMovableRocks.values.flatten().sumOf { input.size - it.y }.toLong()
    }

    val debug = false
    fun printPositions(positions: PositionsByX, fixed: PositionsByX, length: Point, message: String) {
        if (!debug) return
        println(message)
        println()
        (0 until length.y).forEach { y ->
            (0 until length.x).forEach { x ->
                if (Point(x, y) in positions.getOrDefault(x, emptyList()))
                    print("O")
                else if (Point(x, y) in fixed.getOrDefault(x, emptyList())) {
                    print("#")
                } else {
                    print(".")
                }
            }
            println()
        }
    }

    fun part2(input: List<String>): Long {
        val (fixedRocksNorth, movableRocks) = parseInput(input)

        var currentMovableRocks = movableRocks
        val northSouthLength = Point(input[0].length, input.size)
        val eastWestLength = Point(northSouthLength.y, northSouthLength.x)
        val fixedRocksWest = fixedRocksNorth.rotateClockwise(eastWestLength)
        //printPositions(fixedRocksNorth.values.flatten().toSet(), northSouthLength)
        //println()
        //printPositions(fixedRocksWest.values.flatten().toSet(), eastWestLength)

        val fixedRocksSouth = fixedRocksWest.rotateClockwise(northSouthLength)
        val fixedRocksEast = fixedRocksSouth.rotateClockwise(eastWestLength)
//        (0 until (3 + ((1_000_000_000-3) %7))).asSequence().forEach { iter ->
        (0 until (151 + ((1_000_000_000 - 151) % (168 - 151)))).asSequence().forEach { iter ->

            //(0 until (168 + 1) + 100_000_000 % (168 - 151)).asSequence().forEach { iter ->
            // (0 until 1_000_000_000 % (168 - 151)).asSequence().forEach { iter ->
            //(0 until 100_000_000).asSequence().forEach { iter ->
            if (iter > 0) {
                currentMovableRocks = currentMovableRocks.rotateClockwise(northSouthLength)
            }
            // NORTH
            printPositions(currentMovableRocks, fixedRocksNorth, northSouthLength, "Cycle $iter \nNORTH")
            currentMovableRocks = computeNewMovableRockPositions(fixedRocksNorth, currentMovableRocks, iter, "NORTH")
            printPositions(currentMovableRocks, fixedRocksNorth, northSouthLength, "Rolled NORTH")

            // WEST
            currentMovableRocks = currentMovableRocks.rotateClockwise(eastWestLength)
            printPositions(currentMovableRocks, fixedRocksWest, eastWestLength, "Rotated WEST")
            currentMovableRocks = computeNewMovableRockPositions(fixedRocksWest, currentMovableRocks, iter, "WEST")


            printPositions(
                currentMovableRocks
                    .rotateClockwise(northSouthLength)
                    .rotateClockwise(eastWestLength)
                    .rotateClockwise(northSouthLength),
                fixedRocksNorth, northSouthLength, "WEST ADJUSTED to the north"
            )

            printPositions(currentMovableRocks, fixedRocksWest, eastWestLength, "Rolled WEST")

            // SOUTH
            currentMovableRocks = currentMovableRocks.rotateClockwise(northSouthLength)
            printPositions(currentMovableRocks, fixedRocksSouth, northSouthLength, "Rotated SOUTH")
            currentMovableRocks = computeNewMovableRockPositions(fixedRocksSouth, currentMovableRocks, iter, "SOUTH")
            printPositions(currentMovableRocks, fixedRocksSouth, northSouthLength, "Rolled SOUTH")

            // EAST
            currentMovableRocks = currentMovableRocks.rotateClockwise(eastWestLength)
            printPositions(currentMovableRocks, fixedRocksEast, eastWestLength, "Rotated EAST")
            currentMovableRocks = computeNewMovableRockPositions(fixedRocksEast, currentMovableRocks, iter, "EAST")
            printPositions(currentMovableRocks, fixedRocksEast, eastWestLength, "Rolled EAST")

        }

        currentMovableRocks = currentMovableRocks.rotateClockwise(northSouthLength)

        return currentMovableRocks.values.flatten().sumOf { input.size - it.y }.toLong()
    }

// test if implementation meets criteria from the description, like:
    val testInput = readInput2023("Day${day}_test")

// Check test inputs
    //  check(136L, part1(testInput), "Part 1")
    newMovableCache.clear()
    //check(64L, part2(testInput).also { println("Test result part 2 is $it") }, "Part 2")
    newMovableCache.clear()

    val input = readInput2023("Day${day}")

    //part1(input).println()
    newMovableCache.clear()

    part2(input).println()
}