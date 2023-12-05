fun main() {
    val day = "05"

    data class RangeMapEntry(
        val keyRangeStart: Long,
        val valueRangeStart: Long,
        val numElements: Long
    )

    fun Set<RangeMapEntry>.get(k: Long): Long {
        return this.find {
            k in it.keyRangeStart..(it.keyRangeStart + it.numElements)
        }?.let {
            it.valueRangeStart + (k - it.keyRangeStart)
        } ?: k
    }

    data class Almanac(
        val seeds: Set<Long>,
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
            .toSet()

        return Almanac(
            seeds,
            parseMapFromInput("seed-to-soil map:", input),
            parseMapFromInput("soil-to-fertilizer map:", input),
            parseMapFromInput("fertilizer-to-water map:", input),
            parseMapFromInput("water-to-light map:", input),
            parseMapFromInput("light-to-temperature map:", input),
            parseMapFromInput("temperature-to-humidity map:", input),
            parseMapFromInput("humidity-to-location map:", input),
        ).also { it.println() }
    }

    fun part1(input: List<String>): Long {
        val almanac = parseInput(input)

        return almanac.seeds
            .map {
                almanac.seedToSoilMap.get(it)
            }.map {
                almanac.soilToFertilizerMap.get(it)
            }.map {
                almanac.fertilizerToWaterMap.get(it)
            }.map {
                almanac.waterToLightMap.get(it)
            }.map {
                almanac.lightToTemperatureMap.get(it)
            }.map {
                almanac.temperatureToHumidityMap.get(it)
            }.map {
                almanac.humidityToLocationMap.get(it)
            }.minBy { it }
    }


    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${day}_test")
    val testResult = part1(testInput)

    println("Test input result: $testResult")
    check(testResult == 35L)

    val input = readInput("Day${day}")
    part1(input).println()
    part2(input).println()
}
