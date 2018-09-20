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
        assertEquals("CLUBS[A],DIAMONDS[5],HEARTS[],SPADES[2, 8]", hand.toString())
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