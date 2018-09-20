package bridgeTree


import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DeckTest {
    @Test
    fun newDeckShouldHave52Cards() {
        val deck = Deck()
        assertEquals(52, deck.size)
    }

    @Test
    fun newDeckShouldBeInOrder() {
        val deck = Deck()
        var prevCard: Card? = null
        for (card: Card in deck) {
            if (prevCard == null) {
                continue
            }
            assertTrue(card > prevCard)
            prevCard = card
        }

    }

    @Test
    fun shuffledDeckShouldHave52Cards() {
        val deck = Deck()
        val shuffledDeck = deck.shuffle()
        assertEquals(52, shuffledDeck.size)
    }

    @Test
    fun deckWithSpecifiedCardsShouldContainThoseCards() {
        val startingCards = listOf(
                Card(Suit.SPADES, Pip.FIVE),
                Card(Suit.CLUBS, Pip.QUEEN),
                Card(Suit.DIAMONDS, Pip.NINE),
                Card(Suit.HEARTS, Pip.FOUR)
        )
        val deck = Deck(startingCards)
        assertEquals(4, deck.size)
        assertEquals(startingCards[0], deck.pop())
        assertEquals(startingCards[1], deck.pop())
        assertEquals(startingCards[2], deck.pop())
        assertEquals(startingCards[3], deck.pop())
    }

    @Test
    fun deckShouldDeal13Cards() {
        val hands = Deck().shuffle().deal()
        assertEquals(13, hands[0].size)
        assertEquals(13, hands[1].size)
        assertEquals(13, hands[2].size)
        assertEquals(13, hands[3].size)
    }
}