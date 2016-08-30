package com.growse.bridgetree

import WHAT._
import org.junit.Assert._
import org.junit.{Rule, Test}
import org.junit.rules.ExpectedException

/**
  * Created by andrew on 21/08/2016.
  */
class CardsDecksAndPlayersTest {

  @Test
  def TwoPlayOrdersThatAreTheSameShouldBeTheSame(): Unit = {
    val list1 = List(
      Card(Suit.C, Pip.Three),
      Card(Suit.D, Pip.Five),
      Card(Suit.C, Pip.Jack),
      Card(Suit.S, Pip.Four)
    )
    val list2 = List(
      Card(Suit.C, Pip.Three),
      Card(Suit.D, Pip.Five),
      Card(Suit.C, Pip.Jack),
      Card(Suit.S, Pip.Four)
    )
    assertEquals(list1, list2)
    assertEquals(0, list1.compare(list2))
  }

  @Test
  def TwoPlayOrdersThatAreDifferentShouldBeDifferentBecauseTricksAreDifferent(): Unit = {
    val list1 = List(
      Card(Suit.C, Pip.Three),
      Card(Suit.D, Pip.Five),
      Card(Suit.C, Pip.Jack),
      Card(Suit.S, Pip.Four)
    )
    val list2 = List(
      Card(Suit.D, Pip.Three),
      Card(Suit.D, Pip.Five),
      Card(Suit.C, Pip.Jack),
      Card(Suit.S, Pip.Four)
    )
    assertEquals(1, list1.getLeadTricksWon)
    assertEquals(0, list2.getLeadTricksWon)
    assert(list1 > list2)
  }

  @Test
  def TwoPlayOrdersThatAreDifferentShouldBeDifferentBecauseTricksAreSameButCardsAreDifferent(): Unit = {
    val list1 = List(
      Card(Suit.C, Pip.Three),
      Card(Suit.D, Pip.Five),
      Card(Suit.C, Pip.Jack),
      Card(Suit.S, Pip.Four)
    )
    val list2 = List(
      Card(Suit.S, Pip.Three),
      Card(Suit.D, Pip.Five),
      Card(Suit.S, Pip.Jack),
      Card(Suit.S, Pip.Four)
    )
    assertEquals(1, list1.getLeadTricksWon)
    assertEquals(1, list2.getLeadTricksWon)
    assert(list1 < list2)
  }

  @Test
  def PrintPlayOrderShouldPrintSomeStuff(): Unit = {
    val list1 = List(
      Card(Suit.C, Pip.Three),
      Card(Suit.D, Pip.Five),
      Card(Suit.C, Pip.Jack),
      Card(Suit.S, Pip.Four)
    )
    assertEquals(97, list1.printFullPlay.length) // It really is 97 bytes
  }

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
}
