import utils.println
import utils.readInput

enum class SpringStatus { DAMAGED, OPERATIONAL, UNKNOWN }

fun main() {
    val day = "12"

    data class Row(
        val springStatus: List<SpringStatus>,
        val damagedList: List<Int>,
    )

    fun List<SpringStatus>.currentDamaged(): List<Int> {
        val damagedList = mutableListOf<Int>()
        var currentDamagedCount = 0
        this.forEach {
            if (it == SpringStatus.DAMAGED) currentDamagedCount++
            else if (currentDamagedCount > 0) {
                damagedList.add(currentDamagedCount)
                currentDamagedCount = 0
            }
        }
        if (currentDamagedCount > 0) {
            damagedList.add(currentDamagedCount)
        }
        return damagedList
    }

    fun List<SpringStatus>.isPartialValid(damaged: List<Int>): Boolean {
        val currentDamaged = this.currentDamaged()
        return currentDamaged.size <= damaged.size &&
                currentDamaged.indices.all { if (it < currentDamaged.lastIndex) currentDamaged[it] == damaged[it] else currentDamaged[it] <= damaged[it] }
    }

    fun isPartialValid(damaged: List<Int>, currentDamaged: List<Int>, stillDamaged: Int): Boolean {
        return (currentDamaged.isEmpty() || currentDamaged.size <= damaged.size &&
                damaged.subList(0, currentDamaged.size) == currentDamaged)
                && (currentDamaged.size == damaged.size || stillDamaged <= damaged[currentDamaged.lastIndex + 1])
    }

    val cache = mutableMapOf<Triple<Int, List<Int>, Int>, Long>()

    fun Row.numPermutations(idx: Int = 0, currentDamaged: List<Int> = emptyList(), damageStreak: Int = 0): Long {
        val cacheKey = Triple(idx, currentDamaged, damageStreak)
        val totalDamaged = currentDamaged.toMutableList()
        if (cacheKey in cache) {
            return cache[cacheKey]!!
        }
        if (damageStreak > 0)
            totalDamaged += damageStreak
        return if (this.springStatus.size == idx) {
            if (this.damagedList == totalDamaged) 1 else 0
        } else if (this.springStatus[idx] == SpringStatus.UNKNOWN) {
            val damagedPermutationRes = if (isPartialValid(this.damagedList, currentDamaged, damageStreak + 1))
                this.numPermutations(idx + 1, currentDamaged, damageStreak + 1) else 0
            this.numPermutations(idx + 1, totalDamaged, 0) + damagedPermutationRes
        } else {
            val isDamagedInIdx = this.springStatus[idx] == SpringStatus.DAMAGED
            if (isDamagedInIdx && !isPartialValid(this.damagedList, currentDamaged, damageStreak + 1)) {
                0
            } else
                this.numPermutations(
                    idx + 1,
                    if (isDamagedInIdx) currentDamaged else totalDamaged,
                    if (isDamagedInIdx) damageStreak + 1 else 0
                )
        }.also {
            cache[cacheKey] = it
        }
    }

    fun Row.numPermutations2(currentSpringStatus: List<SpringStatus> = emptyList()): Int {
        return if (this.springStatus.size == currentSpringStatus.size) {
            if (currentSpringStatus.currentDamaged() == this.damagedList)
                1 else 0
        } else if (this.springStatus[currentSpringStatus.lastIndex + 1] == SpringStatus.UNKNOWN) {
            if (currentSpringStatus.isPartialValid(this.damagedList))
                this.numPermutations2(currentSpringStatus + SpringStatus.DAMAGED) +
                        this.numPermutations2(currentSpringStatus + SpringStatus.OPERATIONAL)
            else 0
        } else {
            this.numPermutations2(currentSpringStatus + this.springStatus[currentSpringStatus.lastIndex + 1])
        }.also {
            //cache.put(this.springStatus.subList(currentSpringStatus.lastIndex, this.springStatus.lastIndex + 1), it)
        }

    }

    fun parseInput(input: List<String>): List<Row> {
        return input.map { line ->
            val (springsStr, damagedListStr) = line.split(" ").let { it[0] to it[1] }
            val springStatuses = springsStr.map {
                when (it) {
                    '?' -> SpringStatus.UNKNOWN
                    '.' -> SpringStatus.OPERATIONAL
                    '#' -> SpringStatus.DAMAGED
                    else -> throw IllegalArgumentException("$it status not valid")
                }
            }
            val damaged = damagedListStr.split(",").map { it.toInt() }
            Row(springStatuses, damaged)
        }
    }

    fun parseInputPart2(input: List<String>): List<Row> {
        val origRows = parseInput(input)

        return origRows.map { row ->
            val springStatus = (1..5).flatMap { if (it != 5) row.springStatus + SpringStatus.UNKNOWN else row.springStatus }
            val damaged = (1..5).flatMap { row.damagedList }
            Row(springStatus, damaged)
        }
    }

    fun part1(input: List<String>): Long = parseInput(input).sumOf { cache.clear(); it.numPermutations().toLong() }

    fun part2(input: List<String>): Long = parseInputPart2(input).asSequence()
        .mapIndexed { idx, value -> println("iteration $idx"); value }
        .sumOf { cache.clear(); it.numPermutations().toLong().also { println("result=$it") } }

// test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${day}_test")

// Check test inputs
    utils.check(21L, part1(testInput), "Part 1")
    utils.check(525152L, part2(testInput), "Part 2")

    val input = readInput("Day${day}")
    part1(input).println()
    part2(input).println()
}