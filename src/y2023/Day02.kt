package y2023

import utils.println
import utils.readInput2023

typealias CubeColor = String
typealias CubesInSet = Map<CubeColor, Int>

fun main() {
    val day = "02"

    data class Game(
        val id: Int,
        val sets: List<CubesInSet>
    )

    fun parseGame(gameInput: String): Game {
        val (gameId, setOfCubesStr) = gameInput.split(":").let {
            it[0].removePrefix("Game ").toInt() to
                    it[1].removePrefix(" ")
        }

        val setOfCubes = setOfCubesStr.split("; ")
            .map { set -> // Should be eg '3 blue, 4 red, 3 green'
                set.split(", ")
                    .associate { ballCount ->
                        ballCount.split(' ').let {
                            it[1] to it[0].toInt()
                        }
                    }
            }

        return Game(gameId, setOfCubes)
    }

    fun part1(input: List<String>): Int {
        // num cubes: ' only 12 red cubes, 13 green cubes, and 14 blue cubes'
        val maxNumCubesByColor = mapOf(
            "red" to 12,
            "green" to 13,
            "blue" to 14,
        )

        return input
            .map { parseGame(it) }
            .filter { game ->
                game.sets.all { cubeSetInGame ->
                    cubeSetInGame.all { (cubeColor, numCubes) ->
                        maxNumCubesByColor.getOrDefault(cubeColor, 0) >= numCubes
                    }
                }
            }
            .sumOf { it.id }
    }

    fun minNumCubesPerGame(game: Game): CubesInSet {
        val minNumCubes = mutableMapOf<CubeColor, Int>()
        game.sets
            .flatMap { it.entries }
            .forEach { (color, numBalls) ->
                val cur = minNumCubes.getOrDefault(color, 0)
                if (numBalls > cur) {
                    minNumCubes[color] = numBalls
                }
            }

        return minNumCubes
    }

    fun part2(input: List<String>): Int {
        return input
            .map { parseGame(it) }
            .map { minNumCubesPerGame(it) }
            .map { it.values.fold(1) { acc, value -> acc * value } }
            .sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput2023("Day${day}_test")
    val testResult = part2(testInput)

    println("Test input part 2: $testResult")
    check(testResult == 2286)

    val input = readInput2023("Day${day}")
    part1(input).println()
    part2(input).println()
}