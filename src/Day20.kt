import java.util.LinkedList

fun main() {
    val day = "20"

    data class Pulse(
        val isLow: Boolean,
        val targetModule: String,
        val fromModule: String,
    )

    abstract class Module(
        val name: String,
        val outputModules: List<String>
    ) {

        abstract fun receivePulse(pulse: Pulse): List<Pulse>

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Module

            return name == other.name
        }

        override fun hashCode(): Int {
            return name.hashCode()
        }
    }

    class FlipFlopModule(
        name: String,
        outputModules: List<String>,
        var isOn: Boolean = false,

        ) : Module(name, outputModules) {
        override fun receivePulse(pulse: Pulse): List<Pulse> {
            if (!pulse.isLow)
                return emptyList()
            isOn = !isOn
            return outputModules.map { Pulse(!isOn, it, this.name) }
        }

    }

    class Conjunction(
        name: String,
        outputModules: List<String>,
        val isLastLowPulseByInputModuleStr: MutableMap<String, Boolean>,
    ) : Module(name, outputModules) {
        override fun receivePulse(pulse: Pulse): List<Pulse> {
            isLastLowPulseByInputModuleStr[pulse.fromModule] = pulse.isLow

            val isAllHigh = isLastLowPulseByInputModuleStr.all { !it.value }
            return outputModules.map { Pulse(isAllHigh, it, this.name) }
        }
    }

    class Broadcast(
        outputModules: List<String>,
    ) : Module("broadcaster", outputModules) {
        override fun receivePulse(pulse: Pulse): List<Pulse> {
            return outputModules.map { Pulse(pulse.isLow, it, this.name) }
        }
    }

    fun pushButton(modulesByName: Map<String, Module>): Pair<Int, Int> {
        val toSend = LinkedList<Pulse>().apply {
            add(Pulse(true, "broadcaster", "button"))
        }
        var lowPulses = 0
        var highPulses = 0

        while (toSend.isNotEmpty()) {
            val currentPulse = toSend.pop()
            if (currentPulse.isLow) lowPulses++ else highPulses++
            if (currentPulse.targetModule == "output") continue

            //println("pulse: $currentPulse low: $lowPulses high: $highPulses")
            modulesByName[currentPulse.targetModule]?.receivePulse(currentPulse)?.forEach { toSend.add(it) }
        }
        //println("final low: $lowPulses final high: $highPulses")

        return lowPulses to highPulses
    }


    fun isRxPressedASingleTimeWithLow(modulesByName: Map<String, Module>): Boolean {
        val toSend = LinkedList<Pulse>().apply {
            add(Pulse(true, "broadcaster", "button"))
        }

        var numRxPressesWithLow = 0

        while (toSend.isNotEmpty()) {
            val currentPulse = toSend.pop()
            if (currentPulse.targetModule == "output") continue

            if (currentPulse.targetModule == "rx" && currentPulse.isLow) {
                numRxPressesWithLow++
            }
            //println("pulse: $currentPulse low: $lowPulses high: $highPulses")
            modulesByName[currentPulse.targetModule]?.receivePulse(currentPulse)?.forEach { toSend.add(it) }
        }
        //println("final low: $lowPulses final high: $highPulses")

        return numRxPressesWithLow == 1
    }

    fun parseInput(input: List<String>): Map<String, Module> {
        val modulesByName = input
            .map { line ->
                when {
                    line.startsWith("broadcaster") -> {
                        val outputs = line.split(" -> ")[1].split(",").map { it.trim() }
                        Broadcast(outputs)
                    }

                    line[0] == '%' -> {
                        val name = line.split(" -> ")[0].removePrefix("%").trim()
                        val outputs = line.split(" -> ")[1].split(",").map { it.trim() }
                        FlipFlopModule(name, outputs, false)
                    }

                    line[0] == '&' -> {
                        val name = line.split(" -> ")[0].removePrefix("&").trim()
                        val outputs = line.split(" -> ")[1].split(",").map { it.trim() }
                        Conjunction(name, outputs, mutableMapOf())
                    }

                    else -> throw IllegalArgumentException("unexpected entry $line")
                }
            }.associateBy { it.name }

        modulesByName.filter { it.value is Conjunction }.map { it.value as Conjunction }
            .forEach { conjunctionModule ->
                modulesByName.values.filter { it.outputModules.contains(conjunctionModule.name) }.map { it.name }.forEach { inputModule ->
                    conjunctionModule.isLastLowPulseByInputModuleStr.put(inputModule, true)
                }
            }

        return modulesByName
    }

    fun part1(input: List<String>): Long {
        val modulesByName = parseInput(input)

        val allLowsAndHighs = (1..1000)
            .asSequence()
            .map { pushButton(modulesByName) }
            .fold(0 to 0) { accPair, curPair -> accPair.first + curPair.first to accPair.second + curPair.second }

        return allLowsAndHighs.first.toLong() * allLowsAndHighs.second
    }

    fun part2(input: List<String>): Long {
        val modulesByName = parseInput(input)

        var iter = 0L
        do {
            iter++
            if (iter % 1_000_000L == 0L) {
                println("iter $iter")
            }
        } while (!isRxPressedASingleTimeWithLow(modulesByName))
        return iter
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${day}_test")

    // Check test inputs
    check(11687500L, part1(testInput), "Part 1")
    //check(5L, part2(testInput), "Part 2")


    val input = readInput("Day${day}")
    part1(input).println()
    part2(input).println()
}