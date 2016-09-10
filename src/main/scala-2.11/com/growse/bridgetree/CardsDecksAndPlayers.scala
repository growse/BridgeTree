package com.growse.bridgetree

import com.growse.bridgetree.Pip.Pip
import com.growse.bridgetree.Suit.Suit

import scala.annotation.tailrec
import scala.util.Random

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

  def Partner(player: Player): Player = {
    Player((player.id + 2) % this.values.size)
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