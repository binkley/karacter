package hm.binkley.labs.karacter

class Karacter {
    private val layers = mutableListOf<MutableMap<String, Any>>()
    private val cache: MutableMap<String, Any> = mutableMapOf()

    init {
        layers.add(mutableMapOf())
    }

    operator fun get(key: String): Any? = cache[key]
    operator fun set(key: String, value: Any): Any? {
        val old = layers[0].put(key, value)
        updateCache(key)
        return old
    }

    private fun updateCache(key: String) {
        layers.
                reversed().
                filter { it.containsKey(key) }.
                forEach { cache[key] = it[key] as Any }
    }

    fun size() = layers.map { it.size }.sum()
    fun isEmpty() = 0 == size()

    fun commit() {
        layers.add(0, mutableMapOf())
    }

    fun keys(): Set<String> = layers.map { it.keys }.flatten().toSet()

    fun values(key: String): List<Any> {
        return layers.
                filter { it.containsKey(key) }.
                map { it[key] as Any }
    }

    fun clear() = layers[0].clear()
}
