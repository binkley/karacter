package hm.binkley.labs.karacter

import kotlin.collections.MutableMap.MutableEntry

class Karacter
    : AbstractMutableMap<String, Any>() {
    private val layer = mutableMapOf<String, Any>()
    override val entries: MutableSet<MutableEntry<String, Any>> = mutableSetOf()

    override fun put(key: String, value: Any): Any? = layer.put(key, value)
}
