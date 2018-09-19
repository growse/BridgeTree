package bridgeTree

import mu.KotlinLogging

class Round(val players: Array<Player>, private val trumpSuit: Suit? = null) {


    private val logger = KotlinLogging.logger {}

    private val tricks: MutableList<Trick> = ArrayList(13)

    init {
        logger.info { "Yay. New Round." }
        for (player: Player in players) {
            logger.info { "$player has ${player.hand}" }
        }

        var leadIndex = 0

        for (trickNumber: Int in 1..13) {
            val trick = Trick()
            for (i: Int in 0..3) {
                val thisPlayer = players[(i + leadIndex) % 4]
                val playedCard = thisPlayer.playCard(trick)
                if (
                        trick.getPlayedCards()[0] != null &&
                        playedCard.suit != trick.getPlayedCards()[0]!!.card.suit &&
                        thisPlayer.hand.any { card -> card.suit == trick.getPlayedCards()[0]!!.card.suit }
                ) {
                    throw PlayerDidntFollowSuitException("Player $thisPlayer tried to play $playedCard on $trick when player was still holding ${thisPlayer.hand}")
                }
                trick.addCard(thisPlayer, playedCard)
            }
            val winningCard = trick.getWinningCard(trumpSuit)
            val winningCardPlayedIndex = trick.getPlayedCards().indexOf(winningCard)
            leadIndex = (leadIndex + winningCardPlayedIndex) % 4
            logger.info { "$trickNumber: ${winningCard.player} wins trick with ${winningCard.card} ($trick)" }
            tricks.add(trick)
        }
    }

    fun getTricksWon(place1: Place, place2: Place): Int {
        val parp = tricks.count { trick -> trick.getWinningCard(trumpSuit).player.place == place1 || trick.getWinningCard(trumpSuit).player.place == place2 }
        return parp
    }
}
