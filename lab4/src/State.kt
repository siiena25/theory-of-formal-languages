class State {
    var posRules: ArrayList<PosRule> = ArrayList()

    override fun toString(): String {
        var result = StringBuilder("{")
        for (posRule in posRules) {
            result.append(posRule.toString()).append(", ")
        }
        result = StringBuilder(result.substring(0, result.length - 2))
        result.append("}")
        return result.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val state = other as State
        return posRules == state.posRules
    }
}