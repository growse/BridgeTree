package bridgeTree

import java.util.*

class Deck(startingCards: List<Card>? = null) : Stack<Card>() {

    init {
        if (startingCards == null) {
            val brandNewDeckOfCardsInOrderAndEverything: Array<Card?> =
                    Array(Suit.values().size * Pip.values().size) { i ->
                        Card(Suit.values()[i / Pip.values().size], Pip.values()[i % Pip.values().size])
                    }
            this.addAll(brandNewDeckOfCardsInOrderAndEverything)
        } else {
            this.addAll(startingCards.reversed())
        }

    }

    fun shuffle(random: Random? = null): Deck {
        if (random != null) {
            return Deck(this.shuffled(random))
        } else {
            return Deck(this.shuffled())
        }
    }

    private fun pop(i: Int): List<Card> {
        return (1..i).map { this.pop() }
    }

    fun deal(): List<Hand> {
        return (0..3).map { Hand(this.pop(13)) }

    }
}