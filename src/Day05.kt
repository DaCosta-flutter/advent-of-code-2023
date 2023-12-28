import utils.intersectRange
import utils.notIntersected
import utils.println
import utils.readInput

fun main() {
    val day = "05"

    data class RangeMapEntry(
        val keyRangeStart: Long,
        val valueRangeStart: Long,
        val numElements: Long
    )

    fun Set<RangeMapEntry>.intersectRange(k: LongRange): Set<LongRange> {
        val curMatchedKeys = mutableSetOf<LongRange>()
        val curMatchedValues = mutableSetOf<LongRange>()

        this.forEach { rangeMapEntry ->
            val keyRange = rangeMapEntry.keyRangeStart..(rangeMapEntry.keyRangeStart + rangeMapEntry.numElements)
            val matchedKeys = keyRange.intersectRange(k)
            if (!matchedKeys.isEmpty()) {
                val matchedSize = matchedKeys.last - matchedKeys.first + 1
                val matchedValueStart = rangeMapEntry.valueRangeStart + (matchedKeys.first - rangeMapEntry.keyRangeStart)
                val matchedValues = matchedValueStart until matchedValueStart + matchedSize
                curMatchedKeys.add(matchedKeys)
                curMatchedValues.add(matchedValues)
            }
        }

        curMatchedValues.addAll(k.notIntersected(curMatchedKeys))
        return curMatchedValues.filterNot { it.isEmpty() }.toSet()
    }

    fun Set<RangeMapEntry>.intersectAll(keys: Set<LongRange>): Set<LongRange> {
        return keys.flatMap { this.intersectRange(it) }
            .toSet()
    }

    data class Almanac(
        val seeds: Set<Long>,
        val seedsWithRange: Set<LongRange>,
        val seedToSoilMap: Set<RangeMapEntry>,
        val soilToFertilizerMap: Set<RangeMapEntry>,
        val fertilizerToWaterMap: Set<RangeMapEntry>,
        val waterToLightMap: Set<RangeMapEntry>,
        val lightToTemperatureMap: Set<RangeMapEntry>,
        val temperatureToHumidityMap: Set<RangeMapEntry>,
        val humidityToLocationMap: Set<RangeMapEntry>,
    )

    fun parseMapInput(mapInput: List<String>): Set<RangeMapEntry> {
        return mapInput
            .map { lineInput ->
                lineInput.split(" ").map { it.toLong() }.let {
                    RangeMapEntry(it[1], it[0], it[2])
                }
            }.toSet()
    }

    fun parseMapFromInput(initialStr: String, input: List<String>): Set<RangeMapEntry> {
        val inputIdxStart = input.indexOf(initialStr)

        val mapInput = input.filterIndexed { idx, _ -> idx > inputIdxStart }
            .takeWhile { it.isNotBlank() }

        return parseMapInput(mapInput)
    }

    fun parseInput(input: List<String>): Almanac {
        val seeds = input[0].split(": ")[1]
            .split(" ")
            .filter { it.isNotBlank() }
            .map { it.toLong() }

        val seedsWithRange = seeds.chunked(2)
            .map { it[0] until (it[0] + it[1]) }

        return Almanac(
            seeds.toSet(),
            seedsWithRange.toSet(),
            parseMapFromInput("seed-to-soil map:", input),
            parseMapFromInput("soil-to-fertilizer map:", input),
            parseMapFromInput("fertilizer-to-water map:", input),
            parseMapFromInput("water-to-light map:", input),
            parseMapFromInput("light-to-temperature map:", input),
            parseMapFromInput("temperature-to-humidity map:", input),
            parseMapFromInput("humidity-to-location map:", input),
        )
    }

    fun calculateMinDistance(seedsRange: Set<LongRange>, almanac: Almanac): Long {
        return seedsRange
            .map {
                almanac.seedToSoilMap.intersectRange(it)
            }.map {
                almanac.soilToFertilizerMap.intersectAll(it)
            }.map {
                almanac.fertilizerToWaterMap.intersectAll(it)
            }.map {
                almanac.waterToLightMap.intersectAll(it)
            }.map {
                almanac.lightToTemperatureMap.intersectAll(it)
            }.map {
                almanac.temperatureToHumidityMap.intersectAll(it)
            }.map {
                almanac.humidityToLocationMap.intersectAll(it)
            }.flatten()
            .minOf { it.first }
    }

    fun part1(input: List<String>): Long {
        val almanac = parseInput(input)

        return calculateMinDistance(
            almanac.seeds
                .map { it..it }.toSet(), almanac
        )
    }

    fun part2(input: List<String>): Long {
        val almanac = parseInput(input)

        return calculateMinDistance(almanac.seedsWithRange, almanac)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${day}_test")
    val testResult = part2(testInput)

    println("Test input result: $testResult")
    check(testResult == 46L)

    val input = readInput("Day${day}")
    part1(input).println()
    part2(input).println()
}
