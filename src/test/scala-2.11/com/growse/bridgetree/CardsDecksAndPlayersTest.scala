package com.growse.bridgetree

import org.junit.Assert._
import org.junit.{Rule, Test}
import org.junit.rules.ExpectedException

/**
  * Created by andrew on 21/08/2016.
  */
class CardsDecksAndPlayersTest {
  @Test
  def NorthNextPlayerShouldGiveEast(): Unit = {
    assertEquals(Player.East, Player.NextPlayer(Player.North))
  }

  @Test
  def WestNextPlayerShouldGiveEast(): Unit = {
    assertEquals(Player.North, Player.NextPlayer(Player.West))
  }

  @Test
  def CardToStringShouldBehaveSensibly(): Unit = {
    assertEquals("TwoH", Card(Suit.H, Pip.Two).toString)
  }

  @Test
  def CardComparitorForSameSuitShouldPutPicturesAboveNumbers(): Unit = {
    assert(Card(Suit.D, Pip.Five) < Card(Suit.D, Pip.Jack))
  }

  @Test
  def CardComparitorForDifferentSuitsShouldPutMajorsAboveMinors(): Unit = {
    assert(Card(Suit.D, Pip.Ace) < Card(Suit.H, Pip.Two))
  }

  @Test
  def CardParseTestShouldGiveCorrectAnswer(): Unit = {
    assertEquals(Card(Suit.H, Pip.Two), Card.parse("TwoH"))
  }

  val _exception = ExpectedException.none()

  @Rule
  def exception = _exception

  @Test
  def CardParseTestShouldBehaveSensiblyWhenGivenNonsense(): Unit = {
    exception.expect(classOf[NoSuchElementException])
    Card.parse("WIBBLE")
  }

  @Test
  def DeckShouldGive52Cards(): Unit = {
    assertEquals(52, new Deck().cards.size)
  }

  @Test
  def DeckShuffleShouldGiveDifferentCards(): Unit = {
    val deck1 = new Deck().shuffle
    val deck2 = deck1.shuffle
    assertNotEquals(deck1, deck2)
  }

  @Test
  def DeckDealShouldDeal(): Unit = {
    val deck = new Deck().deal(4)
    assertEquals(4, deck._1.size)
    assertEquals(48, deck._2.cards.size)
  }

  @Test
  def DeckToStringShouldProduceCorrectString(): Unit = {
    val cardSeq = List[Card](
      Card(Suit.C, Pip.Three),
      Card(Suit.D, Pip.Five),
      Card(Suit.C, Pip.Jack),
      Card(Suit.S, Pip.Four)
    )
    val deck = new Deck(cardSeq)
    assertEquals("Deck: ThreeC, FiveD, JackC, FourS", deck.toString())
  }
}
