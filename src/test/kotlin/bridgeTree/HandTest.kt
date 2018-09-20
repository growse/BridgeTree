package bridgeTree

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

class HandTest {
    @Test
    fun handShouldDisplayCardsInOrder() {
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
    fun removingCardFromHandShouldLeaveHandWithoutCard() {
        val hand = Hand(listOf(
                Card(Suit.DIAMONDS, Pip.FIVE),
                Card(Suit.SPADES, Pip.TWO),
                Card(Suit.SPADES, Pip.EIGHT),
                Card(Suit.CLUBS, Pip.ACE)
        ))
        val testCard = Card(Suit.CLUBS, Pip.ACE)
        val newHand = hand.removeCard(testCard)
        assertEquals(3, newHand.size)
        assertFalse(newHand.contains(testCard))
    }

    @Test
    fun removingCardFromHandThatIsntThereShouldThrowSomeSortOfException() {
        val hand = Hand(listOf(
                Card(Suit.DIAMONDS, Pip.FIVE),
                Card(Suit.SPADES, Pip.TWO),
                Card(Suit.SPADES, Pip.EIGHT),
                Card(Suit.CLUBS, Pip.ACE)
        ))
        val testCard = Card(Suit.SPADES, Pip.ACE)
        val newHand = hand.removeCard(testCard)
    }
}