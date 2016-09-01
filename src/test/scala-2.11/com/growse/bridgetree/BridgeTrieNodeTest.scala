package com.growse.bridgetree

import org.junit.Assert._

import org.junit.Test

/**
  * Created by andrew on 30/08/2016.
  */
class BridgeTrieNodeTest {

  @Test
  def ToStringMethodShouldOutputCorrectValueOnRootNode(): Unit = {
    val rootNode = new BridgeTrieNode()
    assertEquals("Root node", rootNode.toString)
  }

  @Test
  def ToStringMethodShouldOutputCorrectValueOnCardPlayedNode(): Unit = {
    val rootNode = new BridgeTrieNode()
    val firstNode = new BridgeTrieNode(card = Some(Card(Suit.C, Pip.Three)), parent = Some(rootNode), player = Some(Player.North))
    rootNode.children.+=(firstNode)
    assertEquals("Card 1: ThreeC by North", firstNode.toString)
  }


  @Test
  def AddingASingleCardNodeShouldGiveCorrectNodeProperties(): Unit = {
    val rootTrieNode = new BridgeTrieNode()
    val firstChild = new BridgeTrieNode(null, Some(Card(Suit.S, Pip.Ace)), Some(rootTrieNode), Some(Player.North))
    rootTrieNode.children.+=(firstChild)
    assertEquals(1, rootTrieNode.children.size) // Do we have one child?
    assertEquals(1, firstChild.cardNumberPlayed)
    assertEquals(None, firstChild.parent.get.card) // Is the parent of the first child the root node?
    assertEquals(Player.North, rootTrieNode.children.head.player.get) //Is the first child of the root node a play made by North?
  }

  @Test
  def FullPrintablePlayorderShouldOutputCorrectString(): Unit = {
    val list1 = List(
      Card(Suit.C, Pip.Three),
      Card(Suit.D, Pip.Five),
      Card(Suit.C, Pip.Jack),
      Card(Suit.S, Pip.Four)
    )
    val rootNode = new BridgeTrieNode()
    rootNode.appendCardList(list1)
    assertEquals(81, rootNode.getLeaves.head.getFullPrintablePlayOrder.length) // It really is 81 bytes
  }

  @Test
  def GettingPlayOrderAsShortStringShouldProduceCorrectValue(): Unit = {
    val rootTrieNode = new BridgeTrieNode()
    val firstChild = new BridgeTrieNode(null, Some(Card(Suit.S, Pip.Ace)), Some(rootTrieNode), Some(Player.North))

    rootTrieNode.children.+=(firstChild)
    val secondChild = new BridgeTrieNode(null, Some(Card(Suit.H, Pip.Queen)), Some(firstChild), Some(Player.East))
    firstChild.children.+=(secondChild)
    assertEquals("AceS,QueenH", secondChild.getPlayOrderAsShortString)
  }

  @Test
  def AppendingASequenceOfCardsShouldProduceASaneTrie(): Unit = {
    val rootTrieNode = new BridgeTrieNode()
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
    rootTrieNode.appendCardList(cardSeq)
    assertEquals(12, rootTrieNode.getLeaves.head.cardNumberPlayed) //Was the last card played the 12th?
    assertEquals("ThreeC,FiveD,JackC,FourS,KingS,AceS,JackC,ThreeS,ThreeH,FiveH,JackH,FourD",
      rootTrieNode.getLeaves.head.getPlayOrderAsShortString)
  }

  @Test
  def UnfinishedTrickOutputShouldReturnListOfTrieNodesCorrespondingToTheCurrentUnfinishedTrick(): Unit = {
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
      Card(Suit.H, Pip.Five) // N
    )
    val rootTrieNode = new BridgeTrieNode()
    rootTrieNode.appendCardList(cardSeq)
    assertEquals(
      List[Card](
        Card(Suit.H, Pip.Three),
        Card(Suit.H, Pip.Five)),
      rootTrieNode.getLeaves.head.getThisUnfinisedTrick.get.map(trienode => trienode.card.get))
  }

  @Test
  def GetLastTrickShouldReturnListOfTrieNodesCorrespondingToTheLastCompleteTrick(): Unit = {
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
      Card(Suit.H, Pip.Five) // N
    )
    val rootTrieNode = new BridgeTrieNode()
    rootTrieNode.appendCardList(cardSeq)
    assertEquals(
      List[Card](
        Card(Suit.S, Pip.King),
        Card(Suit.S, Pip.Ace),
        Card(Suit.C, Pip.Jack),
        Card(Suit.S, Pip.Three)),
      rootTrieNode.getLeaves.head.getLastTrick.get.map(trienode => trienode.card.get))
  }

  @Test
  def GetNextPlayerShouldCalculateTheCorrectNextPlayerForAGivenNode(): Unit = {
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
      Card(Suit.H, Pip.Five) // N
    )
    val rootTrieNode = new BridgeTrieNode()
    rootTrieNode.appendCardList(cardSeq)
    assertEquals(Player.East, rootTrieNode.getLeaves.head.getNextPlayer)
  }

  @Test
  def EveryFourthCardShouldOutputTheCorrectTrickWinnerForThatTrick(): Unit = {
    val cardSeq = List[Card](
      Card(Suit.C, Pip.Three), // N
      Card(Suit.D, Pip.Five), // E
      Card(Suit.C, Pip.Jack), // S Win
      Card(Suit.S, Pip.Four), // W

      Card(Suit.S, Pip.King), // S
      Card(Suit.S, Pip.Ace), // W Win
      Card(Suit.C, Pip.Jack), // N
      Card(Suit.S, Pip.Three)
    )
    val rootTrieNode = new BridgeTrieNode()
    rootTrieNode.appendCardList(cardSeq)
    assertEquals(Player.West, rootTrieNode.getLeaves.head.trickWinner.get)
  }

  @Test
  def ATreeShouldCorrectlyWorkOutHowManyTricksWereWonByLeadPairWithASingleTrick(): Unit = {
    val cardSeq = List[Card](
      Card(Suit.C, Pip.Three),
      Card(Suit.D, Pip.Five),
      Card(Suit.C, Pip.Jack),
      Card(Suit.S, Pip.Four)
    )
    val node = new BridgeTrieNode()
    node.appendCardList(cardSeq)
    assertEquals(1, node.getLeaves.size)
    assertEquals(1, node.getLeaves.last.getNSTricksWon)
  }

  @Test
  def ATreeShouldCorrectlyWorkOutHowManyTricksWereWonByLeadPairWithThreeTricks(): Unit = {

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
    val rootNode = new BridgeTrieNode()
    rootNode.appendCardList(cardSeq)
    assertEquals(1, rootNode.getLeaves.size)
    assertEquals(1, rootNode.getLeaves.head.getNSTricksWon) // 1 Trick won for N/S
  }

  @Test
  def ATreeShouldCorrectlyWorkOutHowManyTricksWereWonByLeadPairWithMultipleDifferentPlayorders(): Unit = {
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
    val rootNode = new BridgeTrieNode()
    rootNode.appendCardList(cardSeq1)
    rootNode.appendCardList(cardSeq2)
    assertEquals(2, rootNode.getLeaves.size) // 2 different plays
    assertEquals(1, rootNode.getLeaves.head.getNSTricksWon) // 1 Trick won for N/S
    assertEquals(2, rootNode.getLeaves.last.getNSTricksWon) // 2 Tricks won for N/S

    // Test again, this time NS only wins 1 each time
    val cardSeq3 = List[Card](
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

    val cardSeq4 = List[Card](
      Card(Suit.S, Pip.Three), // N
      Card(Suit.S, Pip.Five), // E
      Card(Suit.S, Pip.Jack), // S Win
      Card(Suit.S, Pip.Four), // W

      Card(Suit.C, Pip.King), // S
      Card(Suit.C, Pip.Ace), // W Win
      Card(Suit.D, Pip.Jack), // N
      Card(Suit.C, Pip.Three), // E

      Card(Suit.H, Pip.King), // W Win
      Card(Suit.H, Pip.Jack), // N
      Card(Suit.H, Pip.Two), // E
      Card(Suit.H, Pip.Four) // S
    )
    val rootNode2 = new BridgeTrieNode()
    rootNode2.appendCardList(cardSeq3)
    rootNode2.appendCardList(cardSeq4)
    assertEquals(1, rootNode2.getLeaves.head.getNSTricksWon) // 1 Trick won for N/S
    assertEquals(1, rootNode2.getLeaves.last.getNSTricksWon) // 1 Trick won for N/S
  }

  @Test
  def TrickWinnerShouldReturnCorrectInSingleSuit(): Unit = {
    val trick = List[Card](
      Card(Suit.C, Pip.Ace),
      Card(Suit.C, Pip.Two),
      Card(Suit.C, Pip.Three),
      Card(Suit.C, Pip.Four)
    )
    val rootNode = new BridgeTrieNode()
    rootNode.appendCardList(trick)
    assertEquals(1, rootNode.getLeaves.size)
    assert(rootNode.getLeaves.head.trickWinner.isDefined)
    assertEquals(Player.North, rootNode.getLeaves.head.trickWinner.get)
  }

  @Test
  def TrickWinnerShouldReturnCorrectInDifferentSuitsWithTrumps(): Unit = {
    val trick = List[Card](
      Card(Suit.C, Pip.Three),
      Card(Suit.D, Pip.Five),
      Card(Suit.C, Pip.Jack),
      Card(Suit.S, Pip.Four)
    )
    val rootNode = new BridgeTrieNode(Suit.S)
    rootNode.appendCardList(trick)
    assertEquals(1, rootNode.getLeaves.size)
    assert(rootNode.getLeaves.head.trickWinner.isDefined)
    assertEquals(Player.West, rootNode.getLeaves.head.trickWinner.get)
  }

  @Test
  def TrickWinnerShouldReturnCorrectInDifferentSuitsInNoTrumps(): Unit = {
    val trick = List[Card](
      Card(Suit.C, Pip.Three),
      Card(Suit.D, Pip.Five),
      Card(Suit.C, Pip.Jack),
      Card(Suit.S, Pip.Four)
    )
    val rootNode = new BridgeTrieNode()
    rootNode.appendCardList(trick)
    assertEquals(1, rootNode.getLeaves.size)
    assert(rootNode.getLeaves.head.trickWinner.isDefined)
    assertEquals(Player.South, rootNode.getLeaves.head.trickWinner.get)
  }
}
