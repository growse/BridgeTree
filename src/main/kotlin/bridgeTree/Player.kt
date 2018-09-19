package bridgeTree

open class Player(val place: Place, val hand: Hand) {

    open fun playCard(trick: Trick, trumpSuit: Suit? = null): Card {
        if (trick.getPlayedCards()[0] == null) {
            return hand.removeAt(0)
        }
        val trickSuit = trick.getPlayedCards()[0]!!.card.suit
        val matchTrickSuit: (Card) -> Boolean = { card -> card.suit.equals(trickSuit) }
        val matchTrumpSuit: (Card) -> Boolean = { card -> card.suit.equals(trumpSuit) }
        if (hand.any(matchTrickSuit)) {
            val cardToPlay = hand.first(matchTrickSuit)
            hand.remove(cardToPlay)
            return cardToPlay
        }

        if (trumpSuit != null && hand.any(matchTrumpSuit)) {
            val cardToPlay = hand.first(matchTrumpSuit)
            hand.remove(cardToPlay)
            return cardToPlay
        }
        val cardToPlay = hand.first()
        hand.remove(cardToPlay)
        return cardToPlay
    }

    override fun toString(): String {
        return "Player $place"
    }
}