package hm.binkley.labs.karacter

class Karacter private constructor(
        private val cache: MutableMap<String, Any> = mutableMapOf(),
        private val layers: MutableList<EditPad<*>> = mutableListOf(),
        private val rules: MutableMap<String, (Karacter, String) -> Any> = mutableMapOf())
    : Map<String, Any> by cache {
    /** @todo Not concurrency-safe */
    private fun keep(layer: EditPad<*>) = apply {
        val keys = cache.keys + layer.keys + rules.keys
        layers.add(0, layer)
        // cache.clear() - TODO: Is Karacter only additive, no keys deleted?
        keys.forEach { cache[it] = value(it) }
    }

    private fun value(key: String) = rule(key).invoke(this, key)

    private fun rule(key: String) = rules.getOrDefault(key, mostRecent(this)) // XXX

    private fun layers(key: String) = layers.filter { key in it }

    private fun copy() = Karacter(cache.toMutableMap(),
            layers.toMutableList(), rules.toMutableMap())

    @Suppress("UNCHECKED_CAST")
    fun <T> values(key: String): List<T> = layers(key).map { it[key] as T }

    @Suppress("UNCHECKED_CAST")
    fun <T> rule(key: String, rule: (Karacter, String) -> T) {
        rules[key] = rule as (Karacter, String) -> Any
        rules.keys.forEach { cache[it] = value(it) }
    }

    override fun toString() = layers.withIndex().
            map { "${layers.size - it.index}: ${it.value}" }.
            // TODO: Makes spurious newline when there are no layers
            joinToString("\n", "All (${layers.size}): $cache\n")

    abstract class EditPad<T : EditPad<T>> protected constructor(
            protected val karacter: Karacter,
            private val name: String,
            private val cache: MutableMap<String, Any> = mutableMapOf())
        : MutableMap<String, Any> by cache {
        fun <U : EditPad<U>> keep(next: (Karacter) -> U): U
                = next(karacter.keep(this))

        fun <U : EditPad<U>> discard(next: (Karacter) -> U) = next(karacter)

        fun whatIf(): Karacter = karacter.copy().keep(this)

        override fun toString() = "$name $cache"
    }

    companion object {
        fun <T : EditPad<T>> newKaracter(next: (Karacter) -> T)
                : Pair<T, Karacter> {
            val karacter = Karacter()
            return next(karacter) to karacter
        }

        fun <T> mostRecent(defaultValue: T): (Karacter, String) -> T
                = { karacter, key ->
            val maybe = karacter.layers(key)
            if (maybe.isEmpty()) defaultValue else maybe.first()[key] as T
        }
    }
}
