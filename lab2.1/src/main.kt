import java.io.File
import kotlin.system.exitProcess

private val EXPRESSION: Regex = Regex("""/^([A-Z])=(?:((?:[a-z]+|\\([a-z]+(?:\\|[a-z]+)+\\))[A-Z](?:\\+(?:[a-z]+|\\([a-z]+(?:\\|[a-z]+)+\\))[A-Z])*)\\+)?([a-z]+|\\([a-z]+(\\|[a-z]+)""")
private val LAST_BRACKET: Regex = Regex("""/(.*)(\\([^)]*\\))/'""")

private var str: String = ""
private fun getLastType(s: String): String? {
    str = s.replace(Regex("""/\*/"""), "")
    while (LAST_BRACKET.matches(s)) {
        str = s.replace(LAST_BRACKET, "$1x")
    }
    return if (str.length == 1) {
        null
    } else if (str.contains("+")) {
        "+"
    } else {
        "*"
    }
}

fun main() {
    val lineList = mutableListOf<String>()
    var inputConstructors = ""
    var inputVariables = ""
    var inputTerm = ""

    File("examples/example1.txt").useLines { lines -> lines.forEach { lineList.add(it) }}

    lineList.forEach { case ->
        val ind = lineList.indexOf(case)
        val pos = case.indexOf('=')
        when (ind) {
            0 -> {
                inputConstructors = case.substring(pos + 1).replace(" ", "")
                println("Input constructors: $inputConstructors")
            }
            1 -> {
                inputVariables = case.substring(pos + 1).replace(" ", "")
                println("Input variables: $inputVariables")
            }
            2 -> {
                inputTerm = case.substring(pos + 1).replace(" ", "")
                println("Input first term: $inputTerm")
            }
            else -> exitProcess(1)
        }
    }

    parseVariableNames(inputVariables)
    parseConstructorNames(inputConstructors)

    println("Parsed variable names: $varNames")
    println("Parsed constructor names: $constructorNames")
    println("Parsed constants: $constantNames\n")

    val term: Any?

    try {
        str = inputTerm
        term = parseTerm()
    } catch (e: Error) {
        println("Unable to parse term: " + e.message)
        exitProcess(1)
    }
}

private var varNames: MutableSet<String>? = mutableSetOf()
private var constructorNames: MutableMap<String, Constructor>? = mutableMapOf()
private var constantNames: MutableSet<String>? = mutableSetOf()

class Variable(var name: String)
class Constant(var value: String)
class Constructor(name: String, args: MutableList<Any>) {
    var name: String? = name
    var arguments: MutableList<Any>? = args

    override fun toString(): String {
        var res = "$name("
        arguments?.forEach {
            when (it) {
                is Variable -> res += it.name
                is Constant -> res += it.value
                else -> res += it
            }
            if (arguments!!.indexOf(it) < arguments!!.size - 1) {
                res += ", "
            }
        }
        res += ")"
        return res
    }
}

private fun parseVariableNames(vars: String) {
    vars.split(',').forEach { varName ->
        varNames!!.add(varName)
    }
}

private fun parseConstructorNames(constructors: String) {
    constructors.split(',').forEach { str ->
        if (str[2] == '0') {
            constantNames!!.add(str[0].toString())
        } else {
            constructorNames?.set(
                str[0].toString(),
                Constructor(str[0].toString(), mutableListOf(str[2].toString()))
            )
        }
    }
}

private fun parseConstant(): Constant? {
    if (str.isNotEmpty()) {
        val const = str[0].toString()
        if (!constantNames!!.contains(const)) {
            throw Error("Unknown constant name: $const")
        }
        str = str.substring(1)
        return Constant(const)
    }
    return null
}

private fun parseVariable(): Variable? {
    if (str.isNotEmpty()) {
        val variable = str[0].toString()
        if (!varNames!!.contains(variable)) {
            throw Error("Unknown variable name: $variable")
        }
        str = str.substring(1)
        return Variable(variable)
    }
    return null
}

private fun parseConstructor(): Constructor? {
    if (str.isNotEmpty()) {
        val constructorName = str[0].toString()
        if (!constructorNames!!.contains(constructorName)) {
            throw Error("Unknown constructor name: $constructorName")
        }
        str = str.substring(1)

        if (str[0] != '(') {
            throw Error("Missing opening brackets: $constructorName")
        } else {
            str = str.substring(1)
        }

        val args = mutableListOf<Any>()
        var arg = parseTerm()
        args.add(arg!!)
        while (str[0] == ',') {
            str = str.substring(1)
            arg = parseTerm()
            args.add(arg!!)
        }

        if (str[0] != ')') {
            throw Error("Missing closing brackets: $constructorName")
        }
        if (args.size != (constructorNames!![constructorName]?.arguments!![0] as String).toInt()) {
            throw Error("Number of arguments doesn't match: $constructorName")
        }

        str = str.substring(1)
        return Constructor(constructorName, args)
    }
    return null
}

private fun parseTerm(): Any? {
    if (str.isNotEmpty()) {
        val elem = str[0].toString()
        return when {
            varNames!!.contains(elem) -> {
                parseVariable()
            }
            constantNames!!.contains(elem) -> {
                parseConstant()
            }
            constructorNames!!.contains(elem) -> {
                parseConstructor()
            }
            else -> {
                throw Error("Invalid expression.")
            }
        }
    }
    return null
}