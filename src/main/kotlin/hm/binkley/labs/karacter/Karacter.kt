package hm.binkley.labs.karacter

class Karacter private constructor(
        private val cache: MutableMap<String, Any> = mutableMapOf(),
        private val layers: MutableList<Map<String, Any>> = mutableListOf(),
        private val rules: MutableMap<String, (Karacter, String) -> Any> = mutableMapOf())
    : Map<String, Any> by cache {
    /** @todo Not concurrency-safe */
    private fun updateCache(layer: EditPad<*>) {
        val keys = cache.keys + layer.keys
        layers.add(0, layer)
        // cache.clear() - TODO: Is Karacter only additive, no keys deleted?
        keys.forEach { cache[it] = value(it) }
    }

    private fun value(key: String): Any {
        if (key in rules)
            return rules[key]?.invoke(this, key) as Any
        else
            return layers.
                    filter { key in it }.
                    first()[key] as Any
    }

    private fun copy() = Karacter(HashMap(cache), ArrayList(layers),
            HashMap(rules))

    @Suppress("UNCHECKED_CAST")
    fun <T> values(key: String): List<T> = layers.
            filter { key in it }.
            map { it[key] as T }

    fun rule(key: String, rule: (Karacter, String) -> Any) {
        rules[key] = rule
    }

    class ScratchPad(karacter: Karacter) : EditPad<ScratchPad>(karacter)

    open class EditPad<T : EditPad<T>> protected constructor(
            protected val karacter: Karacter)
        : MutableMap<String, Any> by HashMap<String, Any>() {
        fun <U : EditPad<U>> keep(next: (Karacter) -> U): U {
            karacter.updateCache(this)
            return next(karacter)
        }

        fun <U : EditPad<U>> discard(next: (Karacter) -> U) = next(karacter)

        fun whatIf(): Karacter = karacter.copy().apply {
            updateCache(this@EditPad)
        }
    }

    companion object {
        fun <T : EditPad<T>> newKaracter(
                next: (Karacter) -> T): Pair<T, Karacter> {
            val karacter: Karacter = Karacter()
            return next(karacter) to karacter
        }
    }
}
