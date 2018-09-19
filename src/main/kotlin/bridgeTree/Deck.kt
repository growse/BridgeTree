package bridgeTree

import java.util.*
import kotlin.collections.ArrayList

class Deck(startingCards: List<Card>?) : Stack<Card>() {
    constructor() : this(null)

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

    fun getHand(numberOfPlayers: Int = 4): Hand {
        val cards = ArrayList<Card>(this.size / numberOfPlayers)
        for (i: Int in 1..(Card.totalNumberOfCards() / numberOfPlayers)) {
            cards.add(this.pop())
        }
        return Hand(cards)
    }

    fun shuffle(random: Random? = null): Deck {
        if (random != null) {
            return Deck(this.shuffled(random))
        } else {
            return Deck(this.shuffled())
        }
    }
}