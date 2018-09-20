package bridgeTree

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class TrickTest {
    @Test
    fun trickShouldReturnCorrectWinningCardInNoTrumps() {
        val trick = Trick()
        trick.addCard(Player(Place.NORTH, Hand(listOf())), Card(Suit.DIAMONDS, Pip.SEVEN))
        trick.addCard(Player(Place.EAST, Hand(listOf())), Card(Suit.HEARTS, Pip.TWO))
        trick.addCard(Player(Place.SOUTH, Hand(listOf())), Card(Suit.DIAMONDS, Pip.TEN))
        trick.addCard(Player(Place.WEST, Hand(listOf())), Card(Suit.DIAMONDS, Pip.THREE))
        assertEquals(Card(Suit.DIAMONDS, Pip.TEN), trick.getWinningCard().card)
        assertEquals(Place.SOUTH, trick.getWinningCard().player.place)

    }

    @Test
    fun tryingToGetTheWinningTrickShouldFailWhenNotEnoughCards() {
        val trick = Trick()
        trick.addCard(Player(Place.NORTH, Hand(listOf())), Card(Suit.DIAMONDS, Pip.SEVEN))
        trick.addCard(Player(Place.EAST, Hand(listOf())), Card(Suit.HEARTS, Pip.TWO))
        trick.addCard(Player(Place.SOUTH, Hand(listOf())), Card(Suit.DIAMONDS, Pip.TEN))
        assertThrows(IncompleteHandException::class.java) {
            trick.getWinningCard()
        }
    }
}