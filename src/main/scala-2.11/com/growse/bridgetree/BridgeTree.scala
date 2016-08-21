package com.growse.bridgetree

import com.growse.bridgetree.Player.Player
import com.growse.bridgetree.Suit.Suit
import com.typesafe.scalalogging.LazyLogging

import scala.collection.immutable.TreeSet
import scala.collection.mutable.ListBuffer
import scala.collection.{SortedSet, mutable}


class BridgeTree(cards: mutable.LinkedHashSet[Card], trumpSuit: Suit = null) extends LazyLogging {
  if (cards == null) {
    throw new IllegalArgumentException("Deck should not be null")
  }

  val cardsPerHand = cards.size / Player.values.size

  def Play() = {
    val hands: mutable.Map[Player, SortedSet[Card]] = generateHandsFromDeck(cards)

    val startingState = new ListBuffer[Card]
    PlayCard(startingState, hands, Player.North, recursionLevel = 0)
  }

  def generateHandsFromDeck(cards: mutable.LinkedHashSet[Card]): mutable.Map[Player, SortedSet[Card]] = {
    val hands = mutable.Map[Player, SortedSet[Card]]()
    for (i <- Player.values) {
      val hand = cards.slice(i.id * cardsPerHand, (i.id + 1) * cardsPerHand)
      hands(i) = collection.SortedSet(hand.toList: _*)
      logger.debug(s"Player $i has hand: ${hands(i)}")
    }
    hands
  }

  def TrickWinner(cards: Seq[Card]): Int = {
    assert(cards.size == 4)
    val winningcard = cards.reduceLeft { (prevWinner, cur) => {
      if (cur.suit == prevWinner.suit && cur > prevWinner) {
        cur
      } else {
        if (trumpSuit != null && cur.suit == trumpSuit && prevWinner.suit != trumpSuit) {
          cur
        } else {
          prevWinner
        }
      }
    }
    }
    cards.indexOf(winningcard)
  }

  def PlayCard(currentState: Seq[Card], hands: mutable.Map[Player, SortedSet[Card]], playerToPlay: Player, recursionLevel: Int): Unit = {
    var actualPlayerToPlay = playerToPlay
    // Work out if were starting a new trick and if so, who won the last one.
    if (currentState.size % 4 == 0 && currentState.nonEmpty) {
      actualPlayerToPlay = Player((playerToPlay.id + TrickWinner(currentState.takeRight(4))) % 4)
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
      logger.debug(s"Final PlaySeq: $currentState. Lead won ${ResultsCounter.calculateLeadTricksWon(currentState)}")
      ResultsCounter.storeResult(currentState)
    }
  }

  case class PlayOrder(cards: Seq[Card], tricksWon: Int) extends Ordered[PlayOrder] {
    override def toString: String = {
      val stringBuilder: StringBuilder = new StringBuilder
      stringBuilder.append(s"Tricks won: $tricksWon.\n")
      var player = Player.North
      cards.zipWithIndex.foreach(card => {
        stringBuilder.append(s"$player plays ${card._1}\n")
        player = Player.NextPlayer(player)
        if ((card._2 + 1) % 4 == 0) {
          val lastTrickWinner = TrickWinner(cards.slice(card._2 - 3, card._2 + 1))
          player = Player((player.id + lastTrickWinner) % 4)
          stringBuilder.append(s"$player wins\n")
        }
      })
      stringBuilder.toString()
    }

    override def compare(that: PlayOrder): Int = {
      this.tricksWon.compare(that.tricksWon)
    }
  }

  object ResultsCounter {
    var ways: TreeSet[PlayOrder] = TreeSet[PlayOrder]()

    def storeResult(playSeq: Seq[Card]): Unit = {
      val leadTricksWon = calculateLeadTricksWon(playSeq)
      ways.+=(PlayOrder(playSeq, leadTricksWon))
    }

    def calculateLeadTricksWon(playSeq: Seq[Card]): Int = {
      val trickswon = Array(0, 0)
      var lastWinner = 0
      for (i <- playSeq.indices by 4) {
        val winnerIndex = TrickWinner(playSeq.slice(i, i + 4))
        val actualWinner = (lastWinner + winnerIndex) % 4
        trickswon(actualWinner % 2) += 1
        lastWinner = actualWinner
      }
      trickswon(0)
    }
  }

}



