typealias WorkInstruction = String
typealias InstructionsByWorkflow = Map<String, List<WorkInstruction>>
typealias Xmas = Map<Char, Int>

fun main() {
    val day = "19"

    /*data class Xmas(
        val x: Int,
        val m: Int,
        val a: Int,
        val s: Int,
    )*/

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

        instructions.forEach { it.println() }
        parts.forEach { it.println() }
        parts.forEach { isAccepted(it, instructions).println() }
        return parts.filter { isAccepted(it, instructions) }.flatMap { it.values }.sum().toLong()
    }

    fun part2(input: List<String>): Long {
        return input.size.toLong()
    }

// test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${day}_test")

// Check test inputs
    check(19114L, part1(testInput), "Part 1")
    check(testInput.size.toLong(), part2(testInput), "Part 2")

    val input = readInput("Day${day}")
    part1(input).println()
    part2(input).println()
}