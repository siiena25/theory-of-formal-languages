import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import kotlin.system.exitProcess


var index = 0
var indexInTransition = 0
var string = ""
var stringInTransition = ""
var transitions: MutableList<Transition> = mutableListOf()
var state: String? = null
var stateInTransition: String? = null
var state2: String? = null
var bottom: String? = null
var letter: String? = null
var stackS: String? = null
var stackS2: String? = null
var stackSs: MutableList<String> = mutableListOf()

fun nextTerminal(): String {
    if (counter == 18) {
        counter++
        return nextTerminal()
    }

    val result = (65 + counter % 26).toChar().toString() + if (counter < 26) "" else counter / 26 - 1
    counter++

    return result
}

fun parseState(s: String): String? {
    var res = ""
    if (s.isEmpty() || s[0] < 'q' || s[0] > 'u') {
        return null
    }
    res += s[0]

    if (s.length > 1 && s[1] in '0'..'9') {
        res += s[1]
    }
    states.add(res)
    return res
}

fun parseStackS(s: String): String? {
    var res = ""
    if (s.isEmpty() || s[0] < 'A' || s[0] > 'Z') {
        return null
    }
    res += s[0]

    if (s.length > 1 && s[1] in '0'..'9') {
        res += s[1]
    }
    stack_ss.add(res)
    return res
}

fun parseLetter(s: String): String? {
    return if (s.isEmpty() || s[0] < 'a' || s[0] > 'z') {
        if (s.length > 1 && s[0] == '!' && s[1] == '!') {
            "ε "
        } else {
            null
        }
    } else {
        s[0].toString()
    }
}

fun parsePDA(str: String): PDA {
    index = 0
    string = str.replace(" ", "")
    transitions = mutableListOf()

    checkAndWriteState()
    checkAndWriteBottom()
    checkCloseBracket()
    checkEndSymbol()
    checkAndWriteTransitions()

    return PDA(state!!, bottom!!, transitions)
}

fun checkAndWriteTransitions() {
    while (index < string.length) {
        val transition = parseTransition(string.substring(index))
        if (transition == null) {
            println("Incorrect input: check transitions.")
            exitProcess(2)
        }
        index += transition.toString().length
        if (transition.afterEndSign) {
            index++
        }
        transitions.add(transition)
    }
}

fun checkEndSymbol() {
    if (string.length <= index || string[index] != '$') {
        println("Incorrect input: check end symbol.")
        exitProcess(2)
    }
    index++
}

fun checkCloseBracket() {
    if (string.length <= index || string[index] != '>') {
        println("Incorrect input: check close bracket.")
        exitProcess(2)
    }
    index++
}

fun checkAndWriteBottom() {
    if (string.length <= index || string[index] != ',') {
        println("Incorrect input: check length of string or ',' .")
        exitProcess(2)
    }
    bottom = parseStackS(string.substring(++index))
    if (bottom.isNullOrEmpty()) {
        println("Incorrect input: check bottom.")
        exitProcess(2)
    }
    index += bottom!!.length
}

fun checkAndWriteStackS() {
    if (stringInTransition.length <= indexInTransition || stringInTransition[indexInTransition] != ',') {
        println("Incorrect input: check length of string or ',' .")
        exitProcess(2)
    }
    stackS = parseStackS(stringInTransition.substring(++indexInTransition))
    if (stackS.isNullOrEmpty()) {
        println("Incorrect input: check stackS.")
        exitProcess(2)
    }
    indexInTransition += stackS!!.length
}

fun checkAndWriteStackS2() {
    while (indexInTransition < stringInTransition.length && stringInTransition[indexInTransition] != '>') {
        stackS2 = parseStackS(stringInTransition.substring(indexInTransition))
        if (stackS2.isNullOrEmpty()) {
            println("Incorrect input: check stackS2.")
            exitProcess(2)
        }
        indexInTransition += stackS2!!.length
        stackSs.add(stackS2!!)
    }
}

fun checkAndWriteState() {
    if (string.length <= index || string[index] != '<') {
        println(string.length <= index)
        println(string[index] != '<')
        println(string)
        println("Incorrect input: check length of string or open bracket.")
        exitProcess(2)
    }
    state = parseState(string.substring(++index))
    if (state.isNullOrEmpty()) {
        println("Incorrect input: check state.")
        exitProcess(2)
    }
    index += state!!.length
}

fun checkAndWriteStateInTransition() {
    if (stringInTransition.length <= indexInTransition || stringInTransition[indexInTransition] != '<') {
        println(stringInTransition.length <= indexInTransition)
        println(stringInTransition[indexInTransition] != '<')
        println(stringInTransition)
        println("Incorrect input: check length of string or open bracket.")
        exitProcess(2)
    }
    stateInTransition = parseState(stringInTransition.substring(++indexInTransition))
    if (stateInTransition.isNullOrEmpty()) {
        println("Incorrect input: check state.")
        exitProcess(2)
    }
    indexInTransition += stateInTransition!!.length
}

fun checkAndWriteState2() {
    state2 = parseState(stringInTransition.substring(indexInTransition))
    if (state2.isNullOrEmpty()) {
        println("Incorrect input: check state.")
        exitProcess(2)
    }
    indexInTransition += state2!!.length
}

fun checkAndWriteLetter() {
    if (stringInTransition.length <= indexInTransition || stringInTransition[indexInTransition] != ',') {
        println("Incorrect input: check length of string or ',' .")
        exitProcess(2)
    }
    letter = parseLetter(stringInTransition.substring(++indexInTransition))
    if (letter.isNullOrEmpty()) {
        println("Incorrect input: check letter.")
        exitProcess(2)
    }
    indexInTransition += letter!!.length
}

fun parseTransition(str: String): Transition? {
    indexInTransition = 0
    stringInTransition = str.replace(" ", "")
    stackSs = mutableListOf()

    checkAndWriteStateInTransition()
    checkAndWriteLetter()
    checkAndWriteStackS()
    checkTransitionSymbol()
    checkAndWriteState2()
    checkDelimiter()
    checkAndWriteStackS2()

    if (stringInTransition.length <= indexInTransition || stringInTransition[indexInTransition] != '>') {
        return null
    }
    val afterEndSign = stringInTransition.length > indexInTransition + 1 && stringInTransition[++indexInTransition] == '$'

    return Transition(stateInTransition!!, letter!!, stackS!!, state2!!, stackSs, afterEndSign)
}

fun checkDelimiter() {
    if (stringInTransition.length <= indexInTransition || stringInTransition[indexInTransition] != ',') {
        println("Incorrect input: check length of string or ',' .")
        exitProcess(2)
    }
    indexInTransition++
}

fun checkTransitionSymbol() {
    if (stringInTransition.length <= indexInTransition + 3 || !stringInTransition.startsWith(">-><", indexInTransition)) {
        println("Incorrect input. Check transition symbols.")
        exitProcess(2)
    }
    indexInTransition += 4
}

@Throws(IOException::class, InterruptedException::class)
fun makeGraphviz(pda: PDA, testPath: String) {
    val processBuilder = ProcessBuilder()
    val command = StringBuilder("echo 'digraph { rankdir=\"LR\";")
    for (tr in pda.transitions) {
        command.append(tr.state_1 + "->" + tr.state_2)
        command.append("[label=\"" + tr.letter_1 + ", " + tr.stack_s1 + "/")
        for (stack_s in tr.stack_s2) {
            command.append(stack_s)
        }
        if (tr.stack_s2.size == 0) {
            command.append("ε")
        }
        command.append("\"]")
    }

    command.append("}' | dot -Tpng -o graphiz_" + getTestName(testPath) + ".png")
    processBuilder.command("bash", "-c", command.toString())
    val process = processBuilder.start()
    val output = StringBuilder()
    val outputErr = StringBuilder()
    var line: String?
    var lineErr: String?
    val reader = BufferedReader(InputStreamReader(process.inputStream))
    while (reader.readLine().also { line = it } != null) {
        output.append(line).append("\n")
    }
    val readerErr = BufferedReader(InputStreamReader(process.errorStream))
    while (readerErr.readLine().also { lineErr = it } != null) {
        outputErr.append(lineErr).append("\n")
    }
}

fun getTestName(testPath: String): String {
    var testName = ""
    for (index in testPath.length - 1 downTo 0) {
        val currentElement = testPath[index]
        if (currentElement != '/') {
            testName += currentElement
        } else {
            break
        }
    }
    testName = testName.reversed().substring(0, testName.length - 4)
    return testName
}

fun createTerminals() {
    for (state1 in states) {
        for (sc in stack_ss) {
            for (state2 in states) {
                terminals[Term(state1, sc, state2)] = nextTerminal()
            }
        }
    }
}

fun getStatePermutations(
    states: MutableList<String>,
    k: Int
): MutableList<ArrayList<String>> {
    val result = ArrayList<ArrayList<String>>()
    val n = states.size
    val numArray = IntArray(k)
    while (true) {
        val permutation = ArrayList<String>()
        for (i in 0 until k) {
            permutation.add(states[numArray[i]])
        }
        result.add(permutation)
        var br = true
        for (i in k - 1 downTo 0) {
            if (numArray[i] < n - 1) {
                numArray[i]++
                br = false
                break
            } else {
                numArray[i] = 0
            }
        }
        if (br) {
            break
        }
    }
    return result
}

fun reachable(
    from: String?,
    rules: MutableList<Rule>,
    to: ArrayList<String?>
): MutableList<String?> {
    to.add(from)
    for (rule in rules) {
        if (rule.from == from) {
            for (nterm in rule.nterms) {
                if (!to.contains(nterm)) {
                    reachable(nterm, rules, to)
                }
            }
        }
    }
    return to
}