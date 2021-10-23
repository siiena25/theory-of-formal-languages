import java.io.File

//регулярное выражение для чисел в двоичной системе, делящихся на 3
private val academicRegex = "(0|1(01*0)*1)+".toRegex()

//для регулярного выражения с использованием отрицания заменяем 0->[^1] и 1->[^0]
private val negationRegex = "([^1]|[^0]([^1][^0]*[^1])*[^0])+".toRegex()

//для регулярного выражения с использованием ленивой итерации Клини заменяем * -> *?
private val lazyRegex = "(0|1(01*?0)*?1)+".toRegex()

private val timeForAcademicRegex: MutableList<Long> = mutableListOf()
private val timeForNegationRegex: MutableList<Long> = mutableListOf()
private val timeForLazyRegex: MutableList<Long> = mutableListOf()

fun main() {
    val lineList = mutableListOf<String>()

    File("tests.txt").useLines { lines -> lines.forEach { lineList.add(it) }}

    lineList.forEach { line ->
        val numberInBinary = line.toBigInteger().toString(2)
        checkMatchWithRegex(numberInBinary, academicRegex, timeForAcademicRegex)
        checkMatchWithRegex(numberInBinary, negationRegex, timeForNegationRegex)
        checkMatchWithRegex(numberInBinary, lazyRegex, timeForLazyRegex)
    }

    println(timeForAcademicRegex)
    println(timeForNegationRegex)
    println(timeForLazyRegex)
}

fun checkMatchWithRegex(numberInBinary: String, academicRegex: Regex, timeForRegex: MutableList<Long>) {
    val startTime = System.nanoTime()
    val check = numberInBinary.matches(academicRegex)
    val totalTime = System.nanoTime() - startTime
    timeForRegex.add(totalTime)
}
