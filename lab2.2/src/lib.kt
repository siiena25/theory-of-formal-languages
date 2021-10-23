fun kleeneIteration(str: String): String {
    var res = ""
    if (str == "") {
        return res
    }
    res += if (str[0] != '(' && str.length != 1) {
        "($str)*"
    } else {
        "$str*"
    }
    return res
}

fun brakes(str: String): String {
    if (str[0] == '(' || str.split("\\+").size < 2) {
        return str
    }
    return "($str)"
}

fun checkBrakes(coefficient: String): Boolean {
    if (getNumberOfBrackets(coefficient) > 1) {
        return true
    }
    if (coefficient.contains("|") && !coefficient.contains("(")) {
        return true
    }
    return false
}

fun getNumberOfBrackets(coefficient: String): Int {
    var count = 0
    coefficient.forEach { char ->
        if (char == '(') {
            count += 1
        }
    }
    return count
}