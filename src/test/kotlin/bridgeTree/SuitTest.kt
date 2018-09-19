package bridgeTree

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class SuitTest {
    @Test
    fun suitShortRender() {
        assertEquals("S", Suit.SPADES.short())
    }
}
