import java.io.File
import kotlin.system.exitProcess

fun main() {
    val lineList = mutableListOf<String>()
    File("examples/example4.txt").useLines { lines ->
        lines.forEach {
            lineList.add(it.split("->")[0].replace(" ", ""))
        }
    }
    var flag = false
    for (i in 0 until lineList.size) {
        for (j in 0 until lineList.size) {
            if (lineList[i].isEmpty()) {
                continue
            }
            var posMax: Int
            if (i == j) {
                flag = true
                posMax = prefix(lineList[i])
            } else {
                posMax = prefix(lineList[i] + "#" + lineList[j])
            }

            if (posMax != 0) {
                println("System may be not confluent.")
                if (flag) {
                    println("Have overlap in " + lineList[i] + ": " + lineList[i].substring(0, posMax) + ".")
                } else {
                    println("Prefix: " + lineList[i].substring(0, posMax))
                }
                exitProcess(1)
            }
        }
    }
    println("System is confluent.")
}

fun prefix(s: String): Int {
    val pi: MutableList<Int> = mutableListOf()
    for (i in s.indices) {
        pi.add(0)
    }
    for (i in 1 until s.length) {
        var j = pi[i - 1]
        while (j > 0 && s[i] != s[j]) {
            j = pi[j - 1]
        }
        if (s[i] == s[j]) {
            j += 1
        }
        pi[i] = j
    }
    return pi.last()
}


