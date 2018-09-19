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
}