package BridgeTree

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CardTest {
    @Test
    fun CardToStringShouldOutputCorrectString() {
        val card = Card(Suit.CLUBS, Pip.SEVEN)
        assertEquals("SEVEN of CLUBS", card.toCardString())
    }
}