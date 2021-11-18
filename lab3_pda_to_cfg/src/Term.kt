import java.util.*

class Term(
    private var state1: String,
    private var stack_s: String,
    var state2: String) {
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val that = other as Term
        return state1 == that.state1 && stack_s == that.stack_s && state2 == that.state2
    }

    override fun hashCode(): Int {
        return Objects.hash(state1, stack_s, state2)
    }
}