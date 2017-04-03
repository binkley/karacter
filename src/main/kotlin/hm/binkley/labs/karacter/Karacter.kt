package hm.binkley.labs.karacter

class Karacter {
    private val layers = mutableListOf<Map<String, Any>>()
    private val cache: MutableMap<String, Any> = mutableMapOf()

    init {
        layers.add(mutableMapOf())
    }

    operator fun get(key: String): Any? = cache[key]

    private fun updateCache(key: String) {
        layers.
                reversed().
                filter { it.containsKey(key) }.
                forEach { cache[key] = it[key] as Any }
    }

    var size: Int = 0
        get() = layers.map { it.size }.sum()

    fun isEmpty() = 0 == size

    fun keys(): Set<String> = layers.map { it.keys }.flatten().toSet()
    fun containsKey(key: String) = keys().contains(key)

    fun values(key: String): List<Any> {
        return layers.
                filter { it.containsKey(key) }.
                map { it[key] as Any }
    }

    inner class EditPad : HashMap<String, Any>() {
        fun commit(): EditPad {
            layers.add(0, this)
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
