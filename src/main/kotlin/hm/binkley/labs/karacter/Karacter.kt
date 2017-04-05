package hm.binkley.labs.karacter

class Karacter private constructor(
        private val cache: MutableMap<String, Any> = mutableMapOf(),
        private val layers: MutableList<Map<String, Any>> = mutableListOf(),
        private val rules: MutableMap<String, (Karacter, String) -> Any> = mutableMapOf())
    : Map<String, Any> by cache {
    /** @todo Not concurrency-safe */
    private fun updateCache(layer: EditPad) {
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

    inner class EditPad : MutableMap<String, Any> by HashMap<String, Any>() {
        fun keep(): EditPad {
            updateCache(this)
            return EditPad()
        }

        fun discard() = EditPad()

        fun whatIf() = copy().apply {
            EditPad().apply {
                putAll(this@EditPad)
                keep()
            }
        }
    }

    companion object {
        fun newKaracter(): Pair<EditPad, Karacter> {
            val karacter: Karacter = Karacter()
            return karacter.EditPad() to karacter
        }
    }
}
