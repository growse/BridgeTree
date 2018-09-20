package bridgeTree

import java.util.*

class Hand(cards: List<Card>) : TreeSet<Card>() {

    fun removeCard(card: Card): Hand {
        return Hand(this.filter { c -> c != card })
    }

    init {
        this.addAll(cards)
    }

    override fun toString(): String {
        return Suit.values().map { suit -> "$suit" + this.filter { card -> card.suit == suit }.map { card -> card.pip.short() } }.joinToString(",")
    }
}