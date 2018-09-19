package bridgeTree


import org.junit.jupiter.api.Assertions.assertEquals
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
        assertEquals(Card(Suit.SPADES, Pip.ACE), deck.pop())
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
        val deck = Deck().shuffle()
        assertEquals(13, deck.getHand().size)
        assertEquals(13, deck.getHand().size)
        assertEquals(13, deck.getHand().size)
        assertEquals(13, deck.getHand().size)
    }
}