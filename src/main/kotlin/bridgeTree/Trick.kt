package bridgeTree

data class PlayedCard(val player: Player, val card: Card)

class Trick {
    private val playedCards = arrayOfNulls<PlayedCard>(4)

    fun getPlayedCards(): List<PlayedCard?> {
        return playedCards.toList()
    }

    fun addCard(player: Player, card: Card) {
        playedCards[playedCards.indexOf(null)] = PlayedCard(player, card)
    }

    fun getWinningCard(trumpSuit: Suit? = null): PlayedCard {
        if (playedCards.contains(null)) {
            throw IncompleteHandException()
        }
        val playedCards = playedCards.map { card -> card!! }
        var winningCard = playedCards[0]
        val leadSuit = playedCards[0].card.suit
        for (i: Int in 1..3) {
            if (
                    (playedCards[i].card.suit == trumpSuit && winningCard.card.suit != trumpSuit) || // First trump
                    (playedCards[i].card.suit == trumpSuit && winningCard.card.suit == trumpSuit && playedCards[i].card > winningCard.card) || // Highest trump so far
                    (playedCards[i].card.suit == leadSuit && winningCard.card.suit != trumpSuit && playedCards[i].card > winningCard.card) // Highest so far following suit if no trumps
            ) {
                winningCard = playedCards[i]

            }

        }
        return winningCard
    }

    override fun toString(): String {
        return "Lead: ${this.playedCards[0]?.player?.place} - ${this.playedCards[0]?.card?.toString()}, ${this.playedCards[1]?.card?.toString()}, ${this.playedCards[2]?.card?.toString()}, ${this.playedCards[3]?.card?.toString()}"
    }
}