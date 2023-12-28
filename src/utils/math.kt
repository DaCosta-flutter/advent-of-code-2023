package utils

fun findLCM(numbers: List<Long>): Long {
    return if (numbers.size == 1) {
        numbers[0]
    } else {
        numbers.fold(1) { acc, num -> findLCMTwoNums(acc, num) }
    }
}

private fun findLCMTwoNums(a: Long, b: Long) = a / findGCD(a, b) * b

fun findGCD(a: Long, b: Long): Long {
    return if (b == 0L) a else findGCD(b, a % b)
}