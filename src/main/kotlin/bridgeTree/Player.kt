package bridgeTree

class Player(val place: Place, var hand: Hand, private val strategies: List<PlayerStrategy> = listOf(LowestCardStrategy())) {

    private var delegatedPlayer: Player = this
    private val tricksSoFar: List<Trick>? = null
    fun playCard(trick: Trick, trumpSuit: Suit? = null, dummyHand: Hand): Card {

        val dummy = this.delegatedPlayer != this

        val delegatedStrategies = if (dummy) this.delegatedPlayer.strategies else strategies

        val cardCandidates = delegatedStrategies.map { s -> s.suggestCard(hand, trick, tricksSoFar, trumpSuit, dummyHand) }

        val cardToPlay = cardCandidates.groupBy { it }.maxBy { it.value.size }!!.key

        hand = hand.removeCard(cardToPlay)

        return cardToPlay
    }

    override fun toString(): String {
        return "Player $place"
    }

    fun setDelegate(player: Player) {
        delegatedPlayer = player
    }
}