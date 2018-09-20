package bridgeTree

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.util.*


class RoundTest {
    @Test
    fun simpleNotrumpsRoundShouldPlay() {
        val random = Random(1L)

        val hands = Deck().shuffle(random).deal()
        val players = arrayOf(Player(Place.NORTH, hands[0]), Player(Place.EAST, hands[1]), Player(Place.SOUTH, hands[2]), Player(Place.WEST, hands[3]))

        val round = Round(players = players)
        assertEquals(5, round.getTricksWon(Place.NORTH, Place.SOUTH))
        assertEquals(8, round.getTricksWon(Place.EAST, Place.WEST))
    }

    @Test
    fun simpleTrumpsRoundShouldPlay() {
        val random = Random(1L)

        val hands = Deck().shuffle(random).deal()
        val players = arrayOf(Player(Place.NORTH, hands[0]), Player(Place.EAST, hands[1]), Player(Place.SOUTH, hands[2]), Player(Place.WEST, hands[3]))

        val round = Round(players = players, trumpSuit = Suit.DIAMONDS)
        assertEquals(7, round.getTricksWon(Place.NORTH, Place.SOUTH))
        assertEquals(6, round.getTricksWon(Place.EAST, Place.WEST))
    }

    @Test
    fun exceptionShouldBeThrownIfPlayerBreaksTheRules() {
        val random = Random(1L)
        val hands = Deck().shuffle(random).deal()
        val players = arrayOf(Player(Place.NORTH, hands[0]), Player(Place.EAST, hands[1]), Player(Place.SOUTH, hands[2], listOf(RuleBreakingStrategy())), Player(Place.WEST, hands[3]))
        assertThrows(PlayerDidntFollowSuitException::class.java) { Round(players = players) }

    }
}

class RuleBreakingStrategy : PlayerStrategy {
    override fun suggestCard(hand: Hand, trick: Trick, tricksSoFar: List<Trick>?, trumpSuit: Suit?, dummyHand: Hand): Card {
        return hand.first()
    }
}