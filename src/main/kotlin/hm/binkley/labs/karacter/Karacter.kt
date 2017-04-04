package hm.binkley.labs.karacter

class Karacter(private val cache: MutableMap<String, Any> = mutableMapOf(),
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
                cache[key] = values<Any>(key).first()
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> values(key: String): List<T> = layers.
            filter { it.containsKey(key) }.
            map { it[key] as T }

    fun rule(key: String, rule: (Karacter, String) -> Any) {
        rules[key] = rule
    }

    inner class EditPad : MutableMap<String, Any> by HashMap<String, Any>() {
        fun commit(): EditPad {
            layers.add(0, this)
            updateCache()
            return EditPad()
        }

        fun whatIf(): Karacter {
            val view = Karacter(HashMap(cache), ArrayList(layers),
                    HashMap(rules))
            with(view.EditPad()) {
                putAll(this@EditPad)
                commit()
            }
            return view
        }
    }

    companion object {
        data class MadeKaracter(val editpad: EditPad,
                                val karacter: Karacter)

        fun makeKaracter(): MadeKaracter {
            val karacter = Karacter()
            val editpad = karacter.EditPad()
            return MadeKaracter(editpad, karacter)
        }
    }
}
