package com.growse.bridgetree

import com.growse.bridgetree.Player.Player
import com.growse.bridgetree.Suit.Suit
import com.typesafe.scalalogging.LazyLogging

import scala.collection.parallel.mutable.ParMap
import scala.collection.{SortedSet, mutable}

class BridgePlayer(cards: mutable.LinkedHashSet[Card], trumpSuit: Suit = null) extends LazyLogging {
  if (cards == null) {
    throw new IllegalArgumentException("Deck should not be null")
  }

  val cardsPerHand = cards.size / Player.values.size

  val rootTrieNode: BridgeTrieNode = new BridgeTrieNode()

  /** *
    * Generate the hands from the deck and kick off the initial play
    */
  def Play() = {
    val hands: mutable.Map[Player, SortedSet[Card]] = generateHandsFromDeck(cards)

    PlayCard(hands, Player.North, recursionLevel = 0, rootTrieNode)
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
    * @param hands          - A map of each hand remaining for each player
    * @param playerToPlay   - The player to play next
    * @param recursionLevel - What depth we're at
    * @param parentTrieNode - The TrieNode of the previous card played
    */
  def PlayCard(hands: mutable.Map[Player, SortedSet[Card]], playerToPlay: Player, recursionLevel: Int, parentTrieNode: BridgeTrieNode): Unit = {
    val actualPlayerToPlay = parentTrieNode.getNextPlayer
    // Work out if were starting a new trick and if so, who won the last one.
    if (parentTrieNode.getLastTrick.isDefined && parentTrieNode.getThisUnfinishedTrick.isEmpty) {
      logger.debug(s"Looks like Player $actualPlayerToPlay won the last trick: ${parentTrieNode.getLastTrick.get}")
    }
    if (hands(actualPlayerToPlay).nonEmpty) {
      hands(actualPlayerToPlay).foreach(card => {
        logger.debug(s"Seeing if player $actualPlayerToPlay can play $card ($recursionLevel)")
        if (
          parentTrieNode.cardNumberPlayed % 4 == 0 // We're the first to play this trick
            || !hands(actualPlayerToPlay).exists(card => card.suit == parentTrieNode.getThisUnfinishedTrick.get.head.card.get.suit) // We can't follow suit
            || parentTrieNode.getThisUnfinishedTrick.get.head.card.get.suit == card.suit // We can follow suit, and this card follows suit
        ) {
          //Play the card
          logger.debug(s"Player $actualPlayerToPlay plays $card")
          val newHands = hands.clone()
          newHands(actualPlayerToPlay) = hands(actualPlayerToPlay).-(card)
          val trieNode = new BridgeTrieNode(trumpSuit, Some(card), Some(parentTrieNode), Some(actualPlayerToPlay))

          parentTrieNode.children.+=(trieNode)

          PlayCard(newHands, Player.NextPlayer(actualPlayerToPlay), recursionLevel + 1, trieNode)
        } else {
          logger.debug(s"Player $actualPlayerToPlay cannot play $card")
        }
      })
    } else {
      assert(parentTrieNode.getPlayOrder.size == cardsPerHand * Player.values.size)
      logger.debug(s"Final PlaySeq: ${parentTrieNode.toString}. NS won ${parentTrieNode.getNSTricksWon}")
    }
  }
}
