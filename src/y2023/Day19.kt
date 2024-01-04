package y2023

import utils.println
import utils.readInput2023
import java.math.BigInteger

typealias WorkInstruction = String
typealias InstructionsByWorkflow = Map<String, List<WorkInstruction>>
typealias Xmas = Map<Char, Int>
typealias XmasRange = Map<Char, IntRange>

fun main() {
    val day = "19"

    fun isAccepted(part: Xmas, instructions: InstructionsByWorkflow): Boolean {
        var currentWorkflow = instructions["in"]!!
        var ruleIdx = 0

        while (true) {
            val currentRule = currentWorkflow[ruleIdx]
            var result: String? = null
            if (currentRule.contains("<")) {
                val (ch, compareTo) = currentRule.split("<").let { it[0][0] to it[1].split(":")[0].toInt() }
                if (part[ch]!! < compareTo) {
                    result = currentRule.split(":")[1]
                }
            } else if (currentRule.contains(">")) {
                val (ch, compareTo) = currentRule.split(">").let { it[0][0] to it[1].split(":")[0].toInt() }
                if (part[ch]!! > compareTo) {
                    result = currentRule.split(":")[1]
                }
            } else {
                result = currentRule
            }

            if (result == null) {
                ruleIdx++
            } else if (result == "A") {
                return true
            } else if (result == "R") {
                return false
            } else {
                ruleIdx = 0
                currentWorkflow = instructions[result]!!
            }

        }
    }

    data class InstructionLocation(
        val idx: Int,
        val workflow: WorkInstruction,
        val validRange: XmasRange
    )

    fun InstructionsByWorkflow.at(location: InstructionLocation) = this[location.workflow]!![location.idx]
    fun InstructionLocation.isAccepted(instructions: InstructionsByWorkflow) =
        this.workflow == "A" || (this.workflow != "R" && instructions.at(this) == "A")

    fun InstructionLocation.isRejected(instructions: InstructionsByWorkflow) =
        this.workflow == "R" || (this.workflow != "A" && instructions.at(this) == "R")


    fun IntRange.intersectRange(with: IntRange): IntRange {
        val startRange = if (with.first > this.first) with.first else this.first
        val endRange = if (with.last < this.last) with.last else this.last
        return startRange..endRange
    }

    fun IntRange.inverse() = if (this.last == 4000) 1 until this.first else (this.last + 1)..4000
    fun XmasRange.changeWithRange(key: Char, range: IntRange) = this.toMutableMap().apply {
        this[key] = this[key]!!.intersectRange(range)
    }.toMap()

    fun InstructionLocation.possibilities(instructions: InstructionsByWorkflow): Set<InstructionLocation> {
        val instruction = instructions.at(this)

        return if ("<" in instruction || ">" in instruction) {
            val isGreaterThan = ">" in instruction
            val (key, valueInCondition) = instruction.split(if (isGreaterThan) ">" else "<")
                .let { it[0][0] to it[1].split(":")[0].toInt() }
            val conditionTrueRange = if (isGreaterThan) (valueInCondition + 1)..4000 else 1 until valueInCondition
            val resultConditionTrueResult = instruction.split(":")[1]

            setOf(
                this.copy(idx = idx + 1, validRange = validRange.changeWithRange(key, conditionTrueRange.inverse())), // FALSE condition
                InstructionLocation(
                    0, resultConditionTrueResult, validRange.changeWithRange(key, conditionTrueRange) // TRUE condition
                )
            )
        } else
            setOf(InstructionLocation(0, instruction, this.validRange))
    }

    fun findRangesEndingInAccepted(
        curInstruction: InstructionLocation,
        instructions: InstructionsByWorkflow,
    ): List<XmasRange> {
        if (curInstruction.isAccepted(instructions)) {
            return listOf(curInstruction.validRange)
        } else if (curInstruction.isRejected(instructions) || curInstruction.validRange.values.any { it.isEmpty() }) {
            return listOf()
        }
        return curInstruction.possibilities(instructions)
            .flatMap { instLocation -> findRangesEndingInAccepted(instLocation, instructions) }
            .filter { it.isNotEmpty() }
    }

    fun XmasRange.totalNumCombinations() = this.values
        .map { it.toSet().size.toBigInteger() }
        .fold(BigInteger.ONE) { acc, v -> acc.multiply(v) }

    fun part1(input: List<String>): Long {
        val instructions = input.takeWhile { it.isNotEmpty() }.associate { line ->
            val (workflow, instructionsStr) = line.split("{").let {
                it[0] to it[1].removeSuffix("}")
            }
            val instructions = instructionsStr.split(",")
            workflow to instructions
        }

        val parts = input.filter { it.startsWith("{") }
            .map { line ->
                val list = line.removePrefix("{").removeSuffix("}").split(",")
                    .map { it.split("=")[1].toInt() }
                mapOf('x' to list[0], 'm' to list[1], 'a' to list[2], 's' to list[3])
            }

        parts.forEach { isAccepted(it, instructions) }
        return parts.filter { isAccepted(it, instructions) }.flatMap { it.values }.sum().toLong()
    }

    fun part2(input: List<String>): BigInteger {
        val instructions = input.takeWhile { it.isNotEmpty() }.associate { line ->
            val (workflow, instructionsStr) = line.split("{").let {
                it[0] to it[1].removeSuffix("}")
            }
            val instructions = instructionsStr.split(",")
            workflow to instructions
        }

        val initialRange = mapOf(
            'x' to 1..4000,
            'm' to 1..4000,
            'a' to 1..4000,
            's' to 1..4000,
        )

        val acceptedGraphs = findRangesEndingInAccepted(InstructionLocation(0, "in", initialRange), instructions)


        return acceptedGraphs.map { it.totalNumCombinations() }.fold(BigInteger.ZERO) { acc, v -> acc.add(v) }
    }

    val testInput = readInput2023("Day${day}_test")

    utils.check(19114L, part1(testInput), "Part 1")
    utils.check(BigInteger.valueOf(167409079868000L), part2(testInput), "Part 2")

    val input = readInput2023("Day${day}")
    part1(input).println()
    part2(input).println()
}