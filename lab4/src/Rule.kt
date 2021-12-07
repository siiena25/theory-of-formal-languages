class Rule(
    var nTerm: String? = null,
    var terms: ArrayList<String> = ArrayList()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val rule = other as Rule
        return nTerm == rule.nTerm && terms == rule.terms
    }

    override fun toString(): String {
        return "Rule{nTerm='$nTerm\',terms=$terms}"
    }
}