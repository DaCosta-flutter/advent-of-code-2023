package y2023

import utils.findLCM
import utils.println
import utils.readInput2023
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

        override fun toString(): String {
            return "Module('$name')"
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

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            if (!super.equals(other)) return false

            other as FlipFlopModule

            return isOn == other.isOn
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + isOn.hashCode()
            return result
        }
    }

    class ConjunctionModule(
        name: String,
        outputModules: List<String>,
        val isLastLowPulseByInputModuleStr: MutableMap<String, Boolean>,
    ) : Module(name, outputModules) {
        override fun receivePulse(pulse: Pulse): List<Pulse> {
            isLastLowPulseByInputModuleStr[pulse.fromModule] = pulse.isLow

            val isAllHigh = isLastLowPulseByInputModuleStr.all { !it.value }
            return outputModules.map { Pulse(isAllHigh, it, this.name) }
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            if (!super.equals(other)) return false

            other as ConjunctionModule

            return isLastLowPulseByInputModuleStr == other.isLastLowPulseByInputModuleStr
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + isLastLowPulseByInputModuleStr.hashCode()
            return result
        }
    }

    class BroadcastModule(
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

            modulesByName[currentPulse.targetModule]?.receivePulse(currentPulse)?.forEach { toSend.add(it) }
        }

        return lowPulses to highPulses
    }

    fun Map<String, Module>.clone(): Map<String, Module> = this.mapValues { (_, origModule) ->
        when (origModule) {
            is ConjunctionModule -> ConjunctionModule(
                origModule.name,
                origModule.outputModules,
                origModule.isLastLowPulseByInputModuleStr.toMutableMap()
            )

            is FlipFlopModule -> FlipFlopModule(origModule.name, origModule.outputModules, origModule.isOn)
            else -> origModule
        }
    }

    fun subgraphOf(
        curModule: String,
        targetModuleName: String,
        modulesByName: Map<String, Module>,
        visited: Set<String> = emptySet()
    ): Set<String> {
        if (curModule == "rx") {
            return emptySet()
        }
        val cur = modulesByName[curModule]!!
        if (curModule == targetModuleName) {
            return visited + curModule
        }

        if (cur.outputModules.isEmpty()) {
            return emptySet()
        }

        return cur.outputModules
            .filter { it !in visited }
            .map { subgraphOf(it, targetModuleName, modulesByName, visited + curModule) }
            .flatten()
            .toSet()
    }

    fun detectCyclePeriod(toCheckAllHighStr: String, modulesToMonitor: Set<Module>, modulesByName: Map<String, Module>): Int {
        val modulesHashcodesPerIter = mutableMapOf<Map<String, Int>, Int>()
        var iter = 0
        val toCheckAllHigh = modulesByName[toCheckAllHighStr]!! as ConjunctionModule

        while (true) {
            val currentHashcodes = modulesByName.filter {
                it.value in modulesToMonitor
            }.map { it.value.name to it.value.hashCode() }.toMap()

            if (currentHashcodes in modulesHashcodesPerIter && toCheckAllHigh.isLastLowPulseByInputModuleStr.all { !it.value }) {
                return iter - modulesHashcodesPerIter[currentHashcodes]!!
            }
            modulesHashcodesPerIter[currentHashcodes] = iter
            pushButton(modulesByName)
            iter++
        }

        throw IllegalArgumentException("reaching here should not happen")
    }

    fun parseInput(input: List<String>): Map<String, Module> {
        val modulesByName = input
            .map { line ->
                when {
                    line.startsWith("broadcaster") -> {
                        val outputs = line.split(" -> ")[1].split(",").map { it.trim() }
                        BroadcastModule(outputs)
                    }

                    line[0] == '%' -> {
                        val name = line.split(" -> ")[0].removePrefix("%").trim()
                        val outputs = line.split(" -> ")[1].split(",").map { it.trim() }
                        FlipFlopModule(name, outputs, false)
                    }

                    line[0] == '&' -> {
                        val name = line.split(" -> ")[0].removePrefix("&").trim()
                        val outputs = line.split(" -> ")[1].split(",").map { it.trim() }
                        ConjunctionModule(name, outputs, mutableMapOf())
                    }

                    else -> throw IllegalArgumentException("unexpected entry $line")
                }
            }.associateBy { it.name }

        modulesByName.filter { it.value is ConjunctionModule }.map { it.value as ConjunctionModule }
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
        fun detectCyclePeriodUntilModule(targetModule: String): Long {
            val subgraph = subgraphOf("broadcaster", targetModule, modulesByName).map { modulesByName[it]!! }.toSet()
            return detectCyclePeriod(targetModule, subgraph, modulesByName.clone()).toLong()
        }

        val modulesWithLsAsOutput = modulesByName
            .filterValues { module -> "ls" in module.outputModules }
            .map { it.key }

        val periodsForRxInputs = modulesWithLsAsOutput
            .map { detectCyclePeriodUntilModule(it) }

        return findLCM(periodsForRxInputs)
    }

    fun printInputAsGraph(input: List<String>) {
        val nodesWithTypeByNodeName = mutableMapOf<String, String>()

        input.flatMap { line ->
            val (fromNodeStr, toNodesStr) = line.split(" -> ")
            val fromNode = if (fromNodeStr == "broadcaster") fromNodeStr else fromNodeStr.substring(1)
            if ("%" in fromNodeStr) {
                nodesWithTypeByNodeName[fromNode] = "INV_$fromNode"
            } else if ("&" in fromNodeStr) {
                nodesWithTypeByNodeName[fromNode] = "CONJ_$fromNode"
            } else {
                nodesWithTypeByNodeName[fromNode] = fromNodeStr
            }
            toNodesStr.split(", ")
                .map { fromNode to it }
        }.onEach { (from, to) ->
            println("${nodesWithTypeByNodeName[from]} -- ${nodesWithTypeByNodeName.getOrDefault(to, to)}")
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput2023("Day${day}_test")
    val input = readInput2023("Day${day}")

    // Check test inputs
    utils.check(11687500L, part1(testInput), "Part 1")
    //check(5L, part2(testInput), "Part 2")


    part1(input).println()
    part2(input).println()
}