package bridgeTree

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.util.*


class RoundTest {
    @Test
    fun SimpleRoundShouldPlay() {
        val random = Random(1L)

        val deck = Deck().shuffle(random)
        val players = arrayOf(Player(Place.NORTH, deck.getHand()), Player(Place.EAST, deck.getHand()), Player(Place.SOUTH, deck.getHand()), Player(Place.WEST, deck.getHand()))

        val round = Round(players = players)
        assertEquals(5, round.getTricksWon(Place.NORTH, Place.SOUTH))
        assertEquals(8, round.getTricksWon(Place.EAST, Place.WEST))
    }

    @Test
    fun ExceptionShouldBeThrownIfPlayerBreaksTheRules() {
        val random = Random(1L)
        val deck = Deck().shuffle(random)
        val players = arrayOf(Player(Place.NORTH, deck.getHand()), RuleBreakingPlayer(Place.EAST, deck.getHand()), Player(Place.SOUTH, deck.getHand()), Player(Place.WEST, deck.getHand()))
        assertThrows(PlayerDidntFollowSuitException::class.java) { Round(players = players) }

    }
}

class RuleBreakingPlayer(place: Place, hand: Hand) : Player(place, hand) {
    override fun playCard(trick: Trick, trumpSuit: Suit?): Card {
        return hand.removeAt(hand.size - 1)

    }

}