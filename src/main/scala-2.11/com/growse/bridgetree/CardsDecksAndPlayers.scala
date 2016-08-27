package com.growse.bridgetree

import com.growse.bridgetree.Pip.Pip
import com.growse.bridgetree.Suit.Suit

import scala.annotation.tailrec
import scala.util.Random
import WHAT._

/**
  * Created by andrew on 21/08/2016.
  */
case class Card(suit: Suit, value: Pip) extends Ordered[Card] {
  override def toString: String = s"$value$suit"

  override def compare(that: Card): Int = {
    if (this.suit == that.suit) {
      this.value.compare(that.value)
    } else {
      this.suit.compare(that.suit)
    }
  }
}

object Card {
  def parse(cardstring: String): Card = {
    Card(Suit.withName(cardstring.takeRight(1)), Pip.withName(cardstring.dropRight(1)))
  }
}

object Pip extends Enumeration {
  type Pip = Value
  val Two, Three, Four, Five, Six, Seven, Eight, Nine, Ten, Jack, Queen, King, Ace = Value
}

object Suit extends Enumeration {
  type Suit = Value
  val C, D, H, S = Value
}

object Player extends Enumeration {
  type Player = Value
  val North, East, South, West = Value

  def NextPlayer(player: Player): Player = {
    Player((player.id + 1) % this.values.size)
  }
}

class Deck private(val cards: List[Card]) {
  def this() = this(
    for {
      s <- Suit.values.toList
      v <- Pip.values.toList
    } yield Card(s, v)
  )

  def shuffle: Deck = new Deck(Random.shuffle(cards))

  def deal(n: Int = 1): (Seq[Card], Deck) = {
    @tailrec def loop(count: Int, c: Seq[Card], d: Deck): (Seq[Card], Deck) = {
      if (count == 0 || d.cards == Nil) (c, d)
      else {
        val card :: deck = d.cards
        loop(count - 1, c :+ card, new Deck(deck))
      }
    }
    loop(n, Seq(), this)
  }
}

class PlayOrder(self: List[Card]) extends Ordered[PlayOrder] {
  override def toString: String = self.toString()
  var trumpSuit: Suit = _

  def setTrumpSuit(trumpSuit: Suit) = {
    this.trumpSuit = trumpSuit
  }

  def getCards: List[Card] = {
    self
  }

  def printFullPlay: String = {
    val stringBuilder: StringBuilder = new StringBuilder
    stringBuilder.append(s"Tricks won: $getLeadTricksWon.\n")
    var player = Player.North
    self.zipWithIndex.foreach(card => {
      stringBuilder.append(s"$player plays ${card._1}\n")
      player = Player.NextPlayer(player)
      if ((card._2 + 1) % 4 == 0) {
        val lastTrickWinner = self.slice(card._2 - 3, card._2 + 1).getTrickWinner(trumpSuit)
        player = Player((player.id + lastTrickWinner) % 4)
        stringBuilder.append(s"$player wins\n")
      }
    })
    stringBuilder.toString()
  }

  def getLeadTricksWon: Int = {
    val trickswon = Array(0, 0)
    var lastWinner = 0
    for (i <- self.indices by 4) {
      val winnerIndex = self.slice(i, i + 4).getTrickWinner(trumpSuit)
      val actualWinner = (lastWinner + winnerIndex) % 4
      trickswon(actualWinner % 2) += 1
      lastWinner = actualWinner
    }
    trickswon(0)
  }

  /** *
    * Given a sequence of 4 cards, work out which play index (0-3) won the trick
    *
    * @param trumpSuit
    * @return
    */
  def getTrickWinner(trumpSuit: Suit): Int = {
    assert(self.size == 4)
    val winningcard = self.reduceLeft { (prevWinner, cur) => {
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
    self.indexOf(winningcard)
  }

  override def compare(that: PlayOrder): Int = {
    val trickCount = self.getLeadTricksWon.compare(that.getLeadTricksWon)
    if (trickCount == 0) {
      self.toString().compareTo(that.getCards.toString)
    } else {
      trickCount
    }
  }
}
