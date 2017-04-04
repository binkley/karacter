package hm.binkley.labs.karacter

class Karacter(private val cache: MutableMap<String, Any> = mutableMapOf())
    : Map<String, Any> by cache {
    private val layers = mutableListOf<Map<String, Any>>()
    private val rules = mutableMapOf<String, (Karacter, String) -> Any>()

    private fun updateCache() {
        cache.clear()
        layers.reversed().
                forEach { layer ->
                    layer.keys.forEach { key ->
                        if (rules.containsKey(key))
                            cache[key] = rules[key]?.invoke(this, key) as Any
                        else
                            cache[key] = layer[key] as Any
                    }
                }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> values(key: String): List<T> = layers.
            filter { it.containsKey(key) }.
            map { it[key] as T }

    fun rule(key: String, rule: (Karacter, String) -> Any): Karacter {
        rules[key] = rule
        return this
    }

    inner class EditPad : MutableMap<String, Any> by HashMap<String, Any>() {
        fun commit(): EditPad {
            layers.add(0, this)
            updateCache()
            return EditPad()
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
