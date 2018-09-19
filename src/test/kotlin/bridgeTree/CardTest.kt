package bridgeTree

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CardTest {
    @Test
    fun cardToStringShouldOutputCorrectString() {
        val card = Card(Suit.CLUBS, Pip.SEVEN)
        assertEquals("SEVEN of CLUBS", card.toLongString())
    }

    @Test
    fun higherCardInSameSuitShouldWin() {
        val card1 = Card(Suit.CLUBS, Pip.FIVE)
        val card2 = Card(Suit.CLUBS, Pip.JACK)
        assertTrue(card2 > card1)
    }

    @Test
    fun lowerCardInHigherSuitShouldWin() {
        val card1 = Card(Suit.CLUBS, Pip.KING)
        val card2 = Card(Suit.SPADES, Pip.TWO)
        assertTrue(card2 > card1)
    }

    @Test
    fun shortStringRenderShouldWork() {
        val card = Card(Suit.CLUBS, Pip.SEVEN)
        assertEquals("7C", card.toString())
    }

}
