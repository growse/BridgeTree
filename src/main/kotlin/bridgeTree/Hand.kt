package bridgeTree

import java.util.*

class Hand(cards: List<Card>) : TreeSet<Card>() {
    fun removeAt(i: Int): Card {
        val card = this.elementAt(i)
        this.remove(card)
        return card
    }

    init {
        this.addAll(cards)
    }

    override fun toString(): String {
        return Suit.values().map { suit -> "$suit" + this.filter { card -> card.suit == suit }.map { card -> card.pip.short() } }.joinToString(",")
    }
}