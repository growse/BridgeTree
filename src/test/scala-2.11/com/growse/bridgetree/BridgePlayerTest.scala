package com.growse.bridgetree

import org.junit.Assert._
import org.junit.{Rule, Test}
import org.junit.rules.ExpectedException
import scala.collection.immutable.TreeSet
import scala.collection.mutable

/**
  * Created by andrew on 21/08/2016.
  */
class BridgePlayerTest {
  val _exception = ExpectedException.none()

  @Rule
  def exception = _exception

  @Test
  def NewBridgeTreeWithNullDeckShouldThrowException(): Unit = {
    exception.expect(classOf[IllegalArgumentException])
    new BridgePlayer(null)
  }

  @Test
  def GenerateHandsFromDeckShouldGiveFourEqualHands(): Unit = {
    val cardSeq = mutable.LinkedHashSet[Card](
      Card(Suit.C, Pip.Three), // N
      Card(Suit.D, Pip.Five), // N

      Card(Suit.C, Pip.Jack), // E
      Card(Suit.S, Pip.Four), // E

      Card(Suit.S, Pip.King), // S
      Card(Suit.S, Pip.Ace), // S

      Card(Suit.C, Pip.Queen), // W
      Card(Suit.S, Pip.Three) // W
    )
    val bridgePlayer = new BridgePlayer(cardSeq)

    val hands = bridgePlayer.generateHandsFromDeck(cardSeq)
    assertEquals(4, hands.size)
    assertEquals(TreeSet(Card(Suit.C, Pip.Three), Card(Suit.D, Pip.Five)), hands(Player.North))
  }

  @Test
  def PlayingARoundOfOneCardEachShouldLeadToExpectedMinMaxTrickCount(): Unit = {
    val startingDeck = mutable.LinkedHashSet[Card](
      Card(Suit.C, Pip.Three),
      Card(Suit.D, Pip.Five),
      Card(Suit.C, Pip.Jack),
      Card(Suit.S, Pip.Four)
    )
    val bridgeTree = new BridgePlayer(startingDeck)
    bridgeTree.Play()

    assertEquals(1, bridgeTree.rootTrieNode.getLeaves.head.getNSTricksWon)
    assertEquals(1, bridgeTree.rootTrieNode.getLeaves.last.getNSTricksWon)
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
    val bridgeTree = new BridgePlayer(startingDeck)
    bridgeTree.Play()

    assertEquals(10,bridgeTree.rootTrieNode.getLeaves.size)
    assertEquals(1, bridgeTree.rootTrieNode.getLeaves.map(t => t.getNSTricksWon).min)
    assertEquals(2, bridgeTree.rootTrieNode.getLeaves.map(t => t.getNSTricksWon).max)
  }

}
