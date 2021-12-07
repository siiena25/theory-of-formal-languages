class PosRule(
    var rule: Rule? = null,
    var posBeforeTerm: Int = 0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val posRule = other as PosRule
        return posBeforeTerm == posRule.posBeforeTerm && rule!!.equals(posRule.rule)
    }

    override fun toString(): String {
        val result = StringBuilder()
        result.append(rule!!.nTerm + " -> ")
        var i = 0
        while (i < posBeforeTerm) {
            result.append(rule!!.terms[i++])
        }
        result.append(".")
        while (i < rule!!.terms.size) {
            result.append(rule!!.terms[i++])
        }
        return result.toString()
    }
}