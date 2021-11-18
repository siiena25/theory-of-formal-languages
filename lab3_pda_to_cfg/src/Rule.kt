class Rule(
    var from: String,
    var term: String?,
    var nterms: MutableList<String?>) {
    override fun toString(): String {
        val result = StringBuilder("$from -> ")
        if (term != null) {
            result.append(term)
        }
        for (term in nterms) {
            result.append(term)
        }
        return result.toString()
    }
}