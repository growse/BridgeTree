package com.growse.bridgetree

import org.junit.Assert._
import org.junit.{Rule, Test}
import org.junit.rules.ExpectedException

import scala.collection.mutable

/**
  * Created by andrew on 21/08/2016.
  */
class BridgeTreeTest {


  val _exception = ExpectedException.none()

  @Rule
  def exception = _exception

  @Test
  def NewBridgeTreeWithNullDeckShouldThrowException(): Unit = {
    exception.expect(classOf[IllegalArgumentException])
    new BridgeTree(null)
  }

  @Test
  def GenerateHandsFromDeckShouldGiveFourEqualHands(): Unit = {
    val cardSeq = mutable.LinkedHashSet[Card](
      Card(Suit.C, Pip.Three),
      Card(Suit.D, Pip.Five),
      Card(Suit.C, Pip.Jack),
      Card(Suit.S, Pip.Four),

      Card(Suit.S, Pip.King),
      Card(Suit.S, Pip.Ace),
      Card(Suit.C, Pip.Queen),
      Card(Suit.S, Pip.Three)
    )
    val bridgeTree = new BridgeTree(cardSeq)

    val hands = bridgeTree.generateHandsFromDeck(cardSeq)
    assertEquals(4, hands.size)
    assertEquals(Card(Suit.C, Pip.Three), hands(Player.North).head)
    assertEquals(2, hands(Player.North).size)
  }

  @Test
  def TrickWinnerShouldReturnCorrectInSameSuit(): Unit = {
    val cards = new mutable.LinkedHashSet[Card]()
    val bridgeTree = new BridgeTree(cards)
    val trick = List[Card](
      Card(Suit.C, Pip.Ace),
      Card(Suit.C, Pip.Two),
      Card(Suit.C, Pip.Three),
      Card(Suit.C, Pip.Four)
    )
    assertEquals(0, bridgeTree.TrickWinner(trick))
  }

  @Test
  def TrickWinnerShouldReturnCorrectInDifferentSuitsWithTrumps(): Unit = {
    val cards = new mutable.LinkedHashSet[Card]()
    val bridgeTree = new BridgeTree(cards,Suit.S)
    val trick = List[Card](
      Card(Suit.C, Pip.Three),
      Card(Suit.D, Pip.Five),
      Card(Suit.C, Pip.Jack),
      Card(Suit.S, Pip.Four)
    )
    assertEquals(3, bridgeTree.TrickWinner(trick))
  }

  @Test
  def TrickWinnerShouldReturnCorrectInDifferentSuitsInNoTrumps(): Unit = {
    val cards = new mutable.LinkedHashSet[Card]()
    val bridgeTree = new BridgeTree(cards)
    val trick = List[Card](
      Card(Suit.C, Pip.Three),
      Card(Suit.D, Pip.Five),
      Card(Suit.C, Pip.Jack),
      Card(Suit.S, Pip.Four)
    )
    assertEquals(2, bridgeTree.TrickWinner(trick))
  }

  @Test
  def ResultsCounterShouldCorrectlyStoreMinMaxWithOneTrick(): Unit = {
    val cards = new mutable.LinkedHashSet[Card]()
    val bridgeTree = new BridgeTree(cards)
    val cardSeq = List[Card](
      Card(Suit.C, Pip.Three),
      Card(Suit.D, Pip.Five),
      Card(Suit.C, Pip.Jack),
      Card(Suit.S, Pip.Four)
    )
    bridgeTree.ResultsCounter.storeResult(cardSeq)
    assertEquals(1, bridgeTree.ResultsCounter.ways.last.tricksWon)
  }

  @Test
  def ResultsCounterShouldCorrectlyStoreMinMaxWithThreeTricks(): Unit = {
    val cards = new mutable.LinkedHashSet[Card]()
    val bridgeTree = new BridgeTree(cards)
    val cardSeq = List[Card](
      Card(Suit.C, Pip.Three), // N
      Card(Suit.D, Pip.Five), // E
      Card(Suit.C, Pip.Jack), // S Win
      Card(Suit.S, Pip.Four), // W

      Card(Suit.S, Pip.King), // S
      Card(Suit.S, Pip.Ace), // W Win
      Card(Suit.C, Pip.Jack), // N
      Card(Suit.S, Pip.Three), // E

      Card(Suit.H, Pip.Three), // W
      Card(Suit.H, Pip.Five), // N
      Card(Suit.H, Pip.Jack), // E Win
      Card(Suit.D, Pip.Four) // S
    )
    bridgeTree.ResultsCounter.storeResult(cardSeq)
    assertEquals(1, bridgeTree.ResultsCounter.ways.last.tricksWon) // 1 Trick won for N/S
  }

  @Test
  def ResultsCounterShouldCorrectlyStoreMinMaxWithMultipleResultsOfThreeTricks(): Unit = {
    val cards = new mutable.LinkedHashSet[Card]()
    val bridgeTree = new BridgeTree(cards)
    val cardSeq1 = List[Card](
      Card(Suit.C, Pip.Three), // N
      Card(Suit.D, Pip.Five), // E
      Card(Suit.C, Pip.Jack), // S Win
      Card(Suit.S, Pip.Four), // W

      Card(Suit.S, Pip.King), // S
      Card(Suit.S, Pip.Ace), // W Win
      Card(Suit.C, Pip.Jack), // N
      Card(Suit.S, Pip.Three), // E

      Card(Suit.H, Pip.Three), // W
      Card(Suit.H, Pip.Five), // N
      Card(Suit.H, Pip.Jack), // E Win
      Card(Suit.D, Pip.Four) // S
    )

    val cardSeq2 = List[Card](
      Card(Suit.S, Pip.Three), // N
      Card(Suit.S, Pip.Five), // E
      Card(Suit.S, Pip.Jack), // S Win
      Card(Suit.S, Pip.Four), // W

      Card(Suit.C, Pip.King), // S
      Card(Suit.C, Pip.Ace), // W Win
      Card(Suit.D, Pip.Jack), // N
      Card(Suit.C, Pip.Three), // E

      Card(Suit.H, Pip.Three), // W
      Card(Suit.H, Pip.Jack), // N Win
      Card(Suit.H, Pip.Two), //
      Card(Suit.H, Pip.Four) // S
    )
    bridgeTree.ResultsCounter.storeResult(cardSeq1)
    bridgeTree.ResultsCounter.storeResult(cardSeq2)
    assertEquals(1, bridgeTree.ResultsCounter.ways.head.tricksWon) // 1 Trick won for N/S
    assertEquals(2, bridgeTree.ResultsCounter.ways.last.tricksWon) // 1 Trick won for N/S
  }

  @Test
  def ResultsCounterShouldCorrectlyStoreMinMaxWithMultipleResultsOfThreeTricksTheOtherWayRound(): Unit = {
    val cards = new mutable.LinkedHashSet[Card]()
    val bridgeTree = new BridgeTree(cards)
    val cardSeq1 = List[Card](
      Card(Suit.C, Pip.Three), // N
      Card(Suit.D, Pip.Five), // E
      Card(Suit.C, Pip.Jack), // S Win
      Card(Suit.S, Pip.Four), // W

      Card(Suit.S, Pip.King), // S
      Card(Suit.S, Pip.Ace), // W Win
      Card(Suit.C, Pip.Jack), // N
      Card(Suit.S, Pip.Three), // E

      Card(Suit.H, Pip.Three), // W
      Card(Suit.H, Pip.Five), // N
      Card(Suit.H, Pip.Jack), // E Win
      Card(Suit.D, Pip.Four) // S
    )

    val cardSeq2 = List[Card](
      Card(Suit.S, Pip.Three), // N
      Card(Suit.S, Pip.Five), // E
      Card(Suit.S, Pip.Jack), // S Win
      Card(Suit.S, Pip.Four), // W

      Card(Suit.C, Pip.King), // S
      Card(Suit.C, Pip.Ace), // W Win
      Card(Suit.D, Pip.Jack), // N
      Card(Suit.C, Pip.Three), // E

      Card(Suit.H, Pip.Three), // W
      Card(Suit.H, Pip.Jack), // N Win
      Card(Suit.H, Pip.Two), //
      Card(Suit.H, Pip.Four) // S
    )
    bridgeTree.ResultsCounter.storeResult(cardSeq2)
    bridgeTree.ResultsCounter.storeResult(cardSeq1)
    assertEquals(1, bridgeTree.ResultsCounter.ways.head.tricksWon) // 1 Trick won for N/S
    assertEquals(2, bridgeTree.ResultsCounter.ways.last.tricksWon) // 1 Trick won for N/S
  }

  @Test
  def PlayingARoundOfOneCardEachShouldLeadToExpectedMinMaxTrickCount(): Unit = {
    val startingDeck = mutable.LinkedHashSet[Card](
      Card(Suit.C, Pip.Three),
      Card(Suit.D, Pip.Five),
      Card(Suit.C, Pip.Jack),
      Card(Suit.S, Pip.Four)
    )
    val bridgeTree = new BridgeTree(startingDeck)
    bridgeTree.Play()
    assertEquals(1, bridgeTree.ResultsCounter.ways.head.tricksWon)
    assertEquals(1, bridgeTree.ResultsCounter.ways.last.tricksWon)
  }

  @Test
  def PlayingARoundOfTwoCardsEachShouldLeadToExpectedMinMaxTrickCount(): Unit = {
    val startingDeck = mutable.LinkedHashSet[Card](
      Card(Suit.C, Pip.Three),
      Card(Suit.D, Pip.Five),
      Card(Suit.C, Pip.Jack),
      Card(Suit.S, Pip.Four),

      Card(Suit.S, Pip.King),
      Card(Suit.S, Pip.Ace),
      Card(Suit.C, Pip.Queen),
      Card(Suit.S, Pip.Three)
    )
    val bridgeTree = new BridgeTree(startingDeck)
    bridgeTree.Play()
    assertEquals(1, bridgeTree.ResultsCounter.ways.head.tricksWon)
    assertEquals(2, bridgeTree.ResultsCounter.ways.last.tricksWon)
  }

}