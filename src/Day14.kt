typealias PositionsByX = Map<Int, Set<Position>>

fun main() {
    val day = "14"

    fun parseInput(input: List<String>): Pair<PositionsByX, PositionsByX> {
        val fixedRocks = mutableSetOf<Position>()
        val movableRocks = mutableSetOf<Position>()

        input.indices.forEach { y ->
            input[y].indices.forEach { x ->
                if (input[y][x] == 'O')
                    movableRocks.add(Position(x, y))
                else if (input[y][x] == '#')
                    fixedRocks.add(Position(x, y))
            }
        }
        return Pair(
            fixedRocks.groupBy { it.x }.mapValues { it.value.toSet() },
            movableRocks.groupBy { it.x }.mapValues { it.value.toSet() })
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

        val newMovableByX = mutableMapOf<Int, Set<Position>>()

        movableByX
            .forEach { (x, movablePositions) ->
                val newMovablePositions = mutableSetOf<Position>()
                val availableFixedByY = fixedRocksByX.getOrDefault(x, emptyList())

                movablePositions.sortedBy { it.y }
                    .forEach { movablePosition ->
                        val newPosition = (newMovablePositions + availableFixedByY)
                            .filter { it.x == movablePosition.x }
                            .filter { it.y < movablePosition.y }
                            .map { it.copy(y = it.y + 1) }
                            .maxByOrNull { it.y } ?: Position(movablePosition.x, 0)
                        newMovablePositions.add(newPosition)
                    }

                newMovableByX[x] = newMovablePositions
            }

        return newMovableByX.also {
            newMovableCache[fixedRocksByX to movableByX] = it to "$cycleNum $orientation"
        }
    }

    fun PositionsByX.rotateClockwise(newTotalLength: Position): PositionsByX {
        return this.values.flatMap { listOfPos ->
            listOfPos.map {
                Position(newTotalLength.x - 1 - it.y, it.x)
            }
        }.groupBy { it.x }.mapValues { it.value.toSet() }
    }

    fun part1(input: List<String>): Long {
        val (fixedRocks, movableRocks) = parseInput(input)

        val newPositionsMovableRocks = computeNewMovableRockPositions(fixedRocks, movableRocks, 0, "NORTH")

        return newPositionsMovableRocks.values.flatten().sumOf { input.size - it.y }.toLong()
    }

    val debug = false
    fun printPositions(positions: PositionsByX, fixed: PositionsByX, length: Position, message: String) {
        if (!debug) return
        println(message)
        println()
        (0 until length.y).forEach { y ->
            (0 until length.x).forEach { x ->
                if (Position(x, y) in positions.getOrDefault(x, emptyList()))
                    print("O")
                else if (Position(x, y) in fixed.getOrDefault(x, emptyList())) {
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
        val northSouthLength = Position(input[0].length, input.size)
        val eastWestLength = Position(northSouthLength.y, northSouthLength.x)
        val fixedRocksWest = fixedRocksNorth.rotateClockwise(eastWestLength)
        //printPositions(fixedRocksNorth.values.flatten().toSet(), northSouthLength)
        //println()
        //printPositions(fixedRocksWest.values.flatten().toSet(), eastWestLength)

        val fixedRocksSouth = fixedRocksWest.rotateClockwise(northSouthLength)
        val fixedRocksEast = fixedRocksSouth.rotateClockwise(eastWestLength)
//        (0 until (3 + ((1_000_000_000-3) %7))).asSequence().forEach { iter ->
        (0 until (151 + ((1_000_000_000 - 151) % (168-151)))).asSequence().forEach { iter ->

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
    val testInput = readInput("Day${day}_test")

// Check test inputs
    //  check(136L, part1(testInput), "Part 1")
    newMovableCache.clear()
    //check(64L, part2(testInput).also { println("Test result part 2 is $it") }, "Part 2")
    newMovableCache.clear()

    val input = readInput("Day${day}")

    //part1(input).println()
    newMovableCache.clear()

    part2(input).println()
}