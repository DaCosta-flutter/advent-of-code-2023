package y2023

import utils.println
import utils.readInput2023

typealias Card = Char

fun main() {
    val day = "07"

    data class Hand(
        val cards: List<Card>,
        val bid: Int,
    )

    fun List<Card>.getTypeRank(): Int {
        val countByCard = this.groupBy { it }.mapValues { it.value.size }
        return when {
            countByCard.any { it.value == 5 } -> 7 // Five of a kind
            countByCard.any { it.value == 4 } -> 6 // FOUR of a kind
            countByCard.any { it.value == 3 } && countByCard.any { it.value == 2 } -> 5 // Full house
            countByCard.any { it.value == 3 } -> 4 // Three of a kind
            countByCard.count { it.value == 2 } == 2 -> 3 // Two pair
            countByCard.count { it.value == 2 } == 1 -> 2 // One pair
            else -> 1 // High card
        }
    }

    fun List<Card>.transformWithJoker(): List<Card> {
        val countByCard = this.groupBy { it }.mapValues { it.value.size }
        val mostCommonCard = countByCard.filter { countByCard.size == 1 || it.key != 'J' }.maxBy { it.value }.key
        return this.map { if (it == 'J') mostCommonCard else it }
    }

    val cardToRank = mapOf(
        'A' to 14,
        'K' to 13,
        'Q' to 12,
        'J' to 11,
        'T' to 10,
    ) + (1..9).associateBy { "$it"[0] }

    fun Card.rankPart1() = cardToRank[this]!!
    fun Card.rankPart2() = if (this == 'J') 0 else cardToRank[this]!!

    fun parseInput(input: List<String>): List<Hand> {
        return input.map { lineStr ->
            lineStr.split(" ").let {
                it[0] to it[1].toInt()
            }
        }.map { (cards, rank) ->
            Hand(cards.toCharArray().toList(), rank)
        }
    }

    fun part1(input: List<String>): Long {
        val hands = parseInput(input)

        return hands.sortedWith(
            compareBy(
                { it.cards.getTypeRank() },
                { it.cards.map { card -> card.rankPart1() }.reduce { acc, value -> acc * 100 + value } }
            ))
            .mapIndexed { idx, hand ->
                (idx + 1) * hand.bid.toLong()
            }.sum()
    }

    fun part2(input: List<String>): Long {
        val hands = parseInput(input)

        return hands.sortedWith(
            compareBy(
                { it.cards.transformWithJoker().getTypeRank() },
                { it.cards.map { card -> card.rankPart2() }.reduce { acc, value -> acc * 100 + value } }
            ))
            .also { it.println() }

            //getHandComparator({ it.cards.transformWithJoker().getTypeRank() }, { it.rankPart2() }))
            .mapIndexed { idx, hand ->
                (idx + 1) * hand.bid.toLong()
            }.sum()
    }

// test if implementation meets criteria from the description, like:
    val testInput = readInput2023("Day${day}_test")
    val testResult = part2(testInput)

    println("Test input result: $testResult")
    check(testResult == 5905L)

    val input = readInput2023("Day${day}")
    part1(input).println()
    part2(input).println()
}