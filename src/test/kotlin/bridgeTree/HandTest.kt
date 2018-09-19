package bridgeTree

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class HandTest {
    @Test
    fun HandShouldDisplayCardsInOrder() {
        val hand = Hand(listOf(
                Card(Suit.DIAMONDS, Pip.FIVE),
                Card(Suit.SPADES, Pip.TWO),
                Card(Suit.SPADES, Pip.EIGHT),
                Card(Suit.CLUBS, Pip.ACE)
        ))
        assertEquals(4, hand.size)
        assertEquals("[AC, 5D, 2S, 8S]", hand.toString())
    }


    @Test
    fun RemoveAtShouldRemoveAndReturnCorrectCard() {
        val hand = Hand(listOf(
                Card(Suit.DIAMONDS, Pip.FIVE),
                Card(Suit.SPADES, Pip.TWO),
                Card(Suit.SPADES, Pip.EIGHT),
                Card(Suit.CLUBS, Pip.ACE)
        ))
        val card = hand.removeAt(0)
        assertEquals(Card(Suit.CLUBS, Pip.ACE), card)
        assertEquals(3, hand.size)
    }
}