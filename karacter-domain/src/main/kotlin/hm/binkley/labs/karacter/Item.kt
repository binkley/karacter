package hm.binkley.labs.karacter

import hm.binkley.labs.karacter.Karacter.MutableEditPad

abstract class Item(karacter: Karacter, name: String, val weight: Weight)
    : MutableEditPad(karacter, name) {
    override fun get(key: String) = when (key) {
        "Weight" -> weight
        else -> super.get(key)
    }
}
