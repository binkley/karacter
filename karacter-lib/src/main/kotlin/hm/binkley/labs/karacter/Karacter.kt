package hm.binkley.labs.karacter

class Karacter private constructor(
        private val cache: MutableMap<String, Any> = mutableMapOf(),
        private val pads: MutableList<MutableEditPad> = mutableListOf())
    : Map<String, Any> by cache {
    /** @todo Not concurrency-safe */
    private fun keep(pad: MutableEditPad) = apply {
        val keys = cache.keys + pad.keys
        pads.add(0, pad)
        keys.forEach { cache[it] = value(it) }
    }

    private fun <T> value(key: String) = rule<T>(key).invoke(this, key)

    private fun <T> rule(key: String) = rules<T>(key).firstOrNull()
            ?: Rule("Most recent (required)") { karacter, _key   ->
        karacter.values<T>(_key).first()
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

    fun toList(): List<Map<String, Any>> = pads

    override fun toString() = pads.withIndex().
            map { "\n${pads.size - it.index}: ${it.value}" }.
            joinToString("", "All (${pads.size}): $cache")

    abstract class MutableEditPad protected constructor(
            protected val karacter: Karacter,
            val name: String,
            private val map: MutableMap<String, Any> = mutableMapOf())
        : MutableMap<String, Any> by map {
        open fun <U : MutableEditPad> keep(next: (Karacter) -> U): U
                = next(karacter.keep(this))

        fun <U : MutableEditPad> discard(next: (Karacter) -> U)
                = next(karacter)

        fun whatIf(): Karacter = karacter.copy().keep(this)

        fun toMap(): Map<String, Any> = map

        override fun toString() = "$name $map"
    }

    class Rule<out T>(val name: String, rule: (Karacter, String) -> T)
        : (Karacter, String) -> T by rule {
        override fun toString() = "[Rule: $name]"
    }

    companion object {
        fun <T : MutableEditPad> newKaracter(next: (Karacter) -> T)
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
