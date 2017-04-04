package hm.binkley.labs.karacter

class Karacter private constructor(
        private val cache: MutableMap<String, Any> = mutableMapOf(),
        private val layers: MutableList<Map<String, Any>> = mutableListOf(),
        private val rules: MutableMap<String, (Karacter, String) -> Any> = mutableMapOf())
    : Map<String, Any> by cache {
    private fun updateCache() {
        val keys = cache.keys + layers[0].keys
        cache.clear()
        keys.forEach { key: String ->
            if (rules.containsKey(key))
                cache[key] = rules[key]?.invoke(this, key) as Any
            else
                cache[key] = layers.
                        filter { it.containsKey(key) }.
                        first()[key] as Any
        }
    }

    private fun copy() = Karacter(HashMap(cache), ArrayList(layers),
            HashMap(rules))

    @Suppress("UNCHECKED_CAST")
    fun <T> values(key: String): List<T> = layers.
            filter { it.containsKey(key) }.
            map { it[key] as T }

    fun rule(key: String, rule: (Karacter, String) -> Any) {
        rules[key] = rule
    }

    inner class EditPad : MutableMap<String, Any> by HashMap<String, Any>() {
        fun keep(): EditPad {
            layers.add(0, this)
            updateCache()
            return EditPad()
        }

        fun discard() = EditPad()

        fun whatIf(): Karacter {
            val view = this@Karacter.copy()
            with(view.EditPad()) {
                putAll(this@EditPad)
                keep()
            }
            return view
        }
    }

    companion object {
        fun newKaracter(): Pair<EditPad, Karacter> {
            val karacter: Karacter = Karacter()
            return karacter.EditPad() to karacter
        }
    }
}
