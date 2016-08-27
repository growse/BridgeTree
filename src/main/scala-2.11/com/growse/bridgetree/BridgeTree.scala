package com.growse.bridgetree

import com.growse.bridgetree.Player.Player
import com.growse.bridgetree.Suit.Suit
import com.typesafe.scalalogging.LazyLogging

import scala.collection.{SortedSet, mutable}
import WHAT._

class BridgeTree(cards: mutable.LinkedHashSet[Card], trumpetySuit: Suit = null) extends LazyLogging {
  if (cards == null) {
    throw new IllegalArgumentException("Deck should not be null")
  }

  val cardsPerHand = cards.size / Player.values.size

  /** *
    * Generate the hands from the deck and kick off the initial play
    */
  def Play() = {
    val hands: mutable.Map[Player, SortedSet[Card]] = generateHandsFromDeck(cards)

    val startingState: List[Card] = List()
    startingState.setTrumpSuit(trumpetySuit)
    PlayCard(startingState, hands, Player.North, recursionLevel = 0)
  }

  /** *
    * Given a set of cards, generate a map of Players / Hands
    *
    * @param cards
    * @return
    */
  def generateHandsFromDeck(cards: mutable.LinkedHashSet[Card]): mutable.Map[Player, SortedSet[Card]] = {
    val hands = mutable.Map[Player, SortedSet[Card]]()
    for (i <- Player.values) {
      val hand = cards.slice(i.id * cardsPerHand, (i.id + 1) * cardsPerHand)
      hands(i) = collection.SortedSet(hand.toList: _*)
      logger.debug(s"Player $i has hand: ${hands(i)}")
    }
    hands
  }


  /** *
    * For a given state of played cards, a list of cards players have in their hands and a player to play next,
    * play a card from the player's hand and then recurse.
    *
    * @param currentState   - A list of cards that have already been played
    * @param hands          - A map of each hand remaining for each player
    * @param playerToPlay   - The player to play next
    * @param recursionLevel - What depth we're at
    */
  def PlayCard(currentState: List[Card], hands: mutable.Map[Player, SortedSet[Card]], playerToPlay: Player, recursionLevel: Int): Unit = {
    var actualPlayerToPlay = playerToPlay
    // Work out if were starting a new trick and if so, who won the last one.
    if (currentState.size % 4 == 0 && currentState.nonEmpty) {
      actualPlayerToPlay = Player((playerToPlay.id + currentState.takeRight(4).getTrickWinner(trumpetySuit)) % 4)
      logger.debug(s"Looks like Player $actualPlayerToPlay won the last trick: ${currentState.takeRight(4)}")
    }
    //logger.debug(s"currentState: $currentState. Player to play: $actualPlayerToPlay")
    if (hands(actualPlayerToPlay).nonEmpty) {
      hands(actualPlayerToPlay).foreach(card => {
        logger.debug(s"Seeing if player $actualPlayerToPlay can play $card ($recursionLevel)")
        if (
          currentState.size % 4 == 0 // We're the first to play this trick
            || !hands(actualPlayerToPlay).exists(card => card.suit == currentState(currentState.size - (currentState.size % 4)).suit) // We can't follow suit
            || currentState(currentState.size - (currentState.size % 4)).suit == card.suit // We can follow suit, and this card follows suit
        ) {
          //Play the card
          logger.debug(s"Player $actualPlayerToPlay plays $card")
          val newHands = hands.clone()
          newHands(actualPlayerToPlay) = hands(actualPlayerToPlay).-(card)

          PlayCard(currentState :+ card, newHands, Player.NextPlayer(actualPlayerToPlay), recursionLevel + 1)
        } else {
          logger.debug(s"Player $actualPlayerToPlay cannot play $card")
        }
      })
    } else {
      assert(currentState.size == cardsPerHand * Player.values.size)

      logger.info(s"Final PlaySeq: $currentState. Lead won ${currentState.getLeadTricksWon}")
      ResultsCounter.storeResult(currentState)
    }
  }

  /** *
    * Maintains a collection of all the different ways that have been played.
    */
  object ResultsCounter {
    var ways: mutable.TreeSet[PlayOrder] = mutable.TreeSet[PlayOrder]()

    /** *
      * Persist the given PlayOrder into the Tree
      *
      * @param playSeq
      */
    def storeResult(playSeq: List[Card]): Unit = {
      logger.info(s"storing a playseq. Now have ${ways.size}")
      ways.+=(playSeq)
    }
  }

}

package object WHAT {
  implicit def enhanceCardList(t: List[Card]): PlayOrder = new PlayOrder(t)
}

