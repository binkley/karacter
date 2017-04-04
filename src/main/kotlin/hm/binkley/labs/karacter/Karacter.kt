package hm.binkley.labs.karacter

class Karacter {
    interface Rule<T> {
        fun apply(values: List<T>): T
    }

    private val layers = mutableListOf<Map<String, Any>>()
    private val cache: MutableMap<String, Any> = mutableMapOf()
    private val rules = mutableMapOf<String, (List<Any>) -> Any>()

    operator fun get(key: String): Any? = cache[key]

    private fun updateCache() {
        cache.clear()
        layers.reversed().
                forEach { layer ->
                    layer.keys.forEach { key ->
                        if (rules.containsKey(key))
                            cache[key] = rules[key]?.invoke(values(key)) as Any
                        else
                            cache[key] = layer[key] as Any
                    }
                }
    }

    val size: Int
        get() = keys.size

    fun isEmpty() = 0 == size

    val keys: Set<String>
        get() = layers.map { it.keys }.flatten().toSet()

    fun containsKey(key: String) = keys.contains(key)

    fun values(key: String): List<Any> = layers.
            filter { it.containsKey(key) }.
            map { it[key] as Any }

    fun <T> rule(key: String, rule: (List<T>) -> T): Karacter {
        rules[key] = rule as ((List<Any>) -> Any)
        return this
    }

    inner class EditPad : HashMap<String, Any>() {
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
