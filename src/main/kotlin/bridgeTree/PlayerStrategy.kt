package bridgeTree

interface PlayerStrategy {
    fun suggestCard(hand: Hand, trick: Trick, tricksSoFar: List<Trick>?, trumpSuit: Suit?, dummyHand: Hand): Card
}

class LowestCardStrategy : PlayerStrategy {
    override fun suggestCard(hand: Hand, trick: Trick, tricksSoFar: List<Trick>?, trumpSuit: Suit?, dummyHand: Hand): Card {
        if (trick.getPlayedCards()[0] == null) {
            return hand.first()
        }
        val trickSuit = trick.getPlayedCards()[0]!!.card.suit
        val matchTrickSuit: (Card) -> Boolean = { card -> card.suit.equals(trickSuit) }
        val matchTrumpSuit: (Card) -> Boolean = { card -> card.suit.equals(trumpSuit) }
        if (hand.any(matchTrickSuit)) {
            return hand.first(matchTrickSuit)
        }

        if (trumpSuit != null && hand.any(matchTrumpSuit)) {
            return hand.first(matchTrumpSuit)
        }
        return hand.first()
    }

}

