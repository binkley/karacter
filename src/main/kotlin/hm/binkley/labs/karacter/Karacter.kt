package hm.binkley.labs.karacter

class Karacter private constructor(
        private val cache: MutableMap<String, Any> = mutableMapOf(),
        private val pads: MutableList<EditPad<*>> = mutableListOf())
    : Map<String, Any> by cache {
    /** @todo Not concurrency-safe */
    private fun keep(pad: EditPad<*>) = apply {
        val keys = cache.keys + pad.keys
        pads.add(0, pad)
        // cache.clear() - TODO: Is Karacter only additive, no keys deleted?
        keys.forEach { cache[it] = value(it) }
    }

    private fun <T> value(key: String) = rule<T>(key).invoke(this, key)

    private fun <T> rule(key: String) = rules<T>(key).firstOrNull()
            ?: Rule("Most recent (required)") { karacter, key ->
        karacter.values<T>(key).first()
    }

    private fun rawValues(key: String) = pads.
            filter { key in it }.
            map { it[key] }

    private fun copy() = Karacter(cache.toMutableMap(), pads.toMutableList())

    @Suppress("UNCHECKED_CAST")
    fun <T> values(key: String) = rawValues(key).
            filterNot { it is Function<*> }.
            map { it as T }

    @Suppress("UNCHECKED_CAST")
    fun <T> rules(key: String) = rawValues(key).
            filter { it is Rule<*> }.
            map { it as Rule<T> }

    override fun toString() = pads.withIndex().
            map { "${pads.size - it.index}: ${it.value}" }.
            // TODO: Makes spurious newline when there are no values
            joinToString("\n", "All (${pads.size}): $cache\n")

    abstract class EditPad<T : EditPad<T>> protected constructor(
            private val karacter: Karacter,
            private val name: String,
            private val cache: MutableMap<String, Any> = mutableMapOf())
        : MutableMap<String, Any> by cache {
        fun <U : EditPad<U>> keep(next: (Karacter) -> U): U
                = next(karacter.keep(this))

        fun <U : EditPad<U>> discard(next: (Karacter) -> U) = next(karacter)

        fun whatIf(): Karacter = karacter.copy().keep(this)

        override fun toString() = "$name $cache"
    }

    class Rule<out T>(private val name: String,
                      private val rule: (Karacter, String) -> T)
        : (Karacter, String) -> T by rule {
        override fun toString() = "[Rule: $name]"
    }

    companion object {
        fun <T : EditPad<T>> newKaracter(next: (Karacter) -> T)
                : Pair<T, Karacter> {
            val karacter = Karacter()
            return next(karacter) to karacter
        }

        fun <T> mostRecent(defaultValue: T)
                = Rule("Most recent (default $defaultValue)")
        { karacter, key ->
            val values = karacter.values<T>(key)
            if (values.isEmpty()) defaultValue
            else values.first()
        }
    }
}
