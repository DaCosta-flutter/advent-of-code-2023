package y2023

import utils.println
import utils.readInput2023

fun main() {
    val day = "04"

    data class Card(
        val id: Int,
        val winningNumbers: Set<Int>,
        val numbersInCard: Set<Int>,
    )

    fun Card.winners(): Set<Int> = numbersInCard intersect winningNumbers

    fun Card.cardIdsWon(): Set<Int> = (1..winners().size).map { this.id + it }.toSet()

    fun calcScorePart1(numRemainingWinners: Int): Int = when (numRemainingWinners) {
        0 -> 0
        1 -> 1
        else -> 2 * calcScorePart1(numRemainingWinners - 1)
    }

    fun parseInput(input: List<String>): List<Card> {
        return input
            .map { lineStr ->
                val initialSplit = lineStr.split(":")
                val cardId = initialSplit[0].split(" ").last().toInt()
                val (winningStr, numbersStr) = initialSplit[1].split(" | ").let {
                    it[0] to it[1]
                }
                val winningNumbers = winningStr.split(" ")
                    .filter { it.isNotBlank() }
                    .map { it.toInt() }.toSet()
                val numbersInCard = numbersStr.split(" ")
                    .filter { it.isNotBlank() }
                    .map { it.toInt() }.toSet()
                Card(cardId, winningNumbers, numbersInCard)
            }
    }

    fun part1(input: List<String>): Int {
        val cards = parseInput(input)

        return cards.sumOf { calcScorePart1(it.winners().size) }
    }

    fun Card.calculateTotalCardsWon(cardIdsById: Map<Int, Card>): Int {
        return this.cardIdsWon()
            .sumOf { cardIdsById[it]!!.calculateTotalCardsWon(cardIdsById) } + 1
    }

    fun part2(input: List<String>): Int {
        val cards = parseInput(input)
            .associateBy { it.id }

        return cards.values.sumOf { it.calculateTotalCardsWon(cards) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput2023("Day${day}_test")
    val testResult = part2(testInput)

    println("Test input result: $testResult")
    check(testResult == 30)

    val input = readInput2023("Day${day}")
    part1(input).println()
    part2(input).println()
}
