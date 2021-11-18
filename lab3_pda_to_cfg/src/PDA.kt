class PDA(
    var state: String,
    var bottom: String,
    var transitions: MutableList<Transition>) {
    override fun toString(): String {
        val result = StringBuilder("<$state,$bottom>$")
        for (trans in transitions) {
            result.append(trans.toString())
            if (trans.afterEndSign) {
                result.append("$")
            }
        }
        return result.toString()
    }
}