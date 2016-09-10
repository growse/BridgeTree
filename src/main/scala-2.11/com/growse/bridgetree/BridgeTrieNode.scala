package com.growse.bridgetree


import com.growse.bridgetree.Player.Player
import com.growse.bridgetree.Suit.Suit

import scala.annotation.tailrec
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * Created by andrew on 27/08/2016.
  */
class BridgeTrieNode(val trumpSuit: Suit = null, val card: Option[Card] = None, val parent: Option[BridgeTrieNode] = None, val player: Option[Player] = None) extends Ordered[BridgeTrieNode] {

  val cardNumberPlayed = this.getCardNumberPlayed
  var trickWinner: Option[Player] = None
  if (cardNumberPlayed % 4 == 0) {
    trickWinner = this.getLastTrickWinner
  }


  def getNSTricksWon: Int = {
    var node = this
    var counter = 0
    while (node.parent.isDefined) {
      if (node.getThisUnfinisedTrick.isEmpty) {
        if (node.trickWinner.isDefined && (node.trickWinner.get == Player.North || node.trickWinner.get == Player.South)) {
          counter += 1
        }
      }
      node = node.parent.get
    }
    counter
  }

  def getNextPlayer: Player = {
    if (getThisUnfinisedTrick.isEmpty && this.parent.isDefined) {
      return this.getLastTrickWinner.get
    } else {
      if (player.isDefined) {
        Player.NextPlayer(player.get)
      } else {
        Player.North //Start with North
      }
    }
  }

  private def getLastTrickWinner: Option[Player] = {
    val lastTrick = getLastTrick
    if (lastTrick.isDefined) {
      val winningcard = lastTrick.get.reduceLeft { (prevWinner, cur) => {
        if (cur.card.get.suit == prevWinner.card.get.suit && cur.card.get > prevWinner.card.get) {
          cur
        } else {
          if (trumpSuit != null && cur.card.get.suit == trumpSuit && prevWinner.card.get.suit != trumpSuit) {
            cur
          } else {
            prevWinner
          }
        }
      }
      }
      winningcard.player
    } else {
      None
    }
  }

  def getLastTrick: Option[List[BridgeTrieNode]] = {
    val cards: mutable.MutableList[BridgeTrieNode] = new mutable.MutableList[BridgeTrieNode]()
    var node = this
    while (cards.size < 4) {
      if (node.cardNumberPlayed % 4 == 0 || cards.nonEmpty) {
        cards.+=(node)
      }
      if (node.parent.isEmpty) {
        return None
      }
      node = node.parent.get
    }
    Some(cards.reverse.toList)
  }

  def getThisUnfinisedTrick: Option[List[BridgeTrieNode]] = {
    var node = this
    if (node.cardNumberPlayed % 4 == 0) {
      return None
    }
    var ret: mutable.MutableList[BridgeTrieNode] = mutable.MutableList[BridgeTrieNode]()
    while (node.cardNumberPlayed % 4 != 0) {
      ret.+=(node)
      node = node.parent.get
    }
    Some(ret.reverse.toList)
  }

  private def getCardNumberPlayed: Int = {
    var counter = 0
    var node = this
    while (node.parent.isDefined) {
      counter += 1
      node = node.parent.get
    }
    counter
  }

  def getPlayOrder: List[Card] = {
    val cardList = mutable.MutableList[Card]()
    var node = this
    while (node.parent.isDefined && node.card.isDefined) {
      cardList.+=(node.card.get)
      node = node.parent.get
    }
    cardList.reverse.toList
  }

  def getPlayOrderAsShortString: String = {
    getPlayOrder.mkString(",")
  }

  def getFullPrintablePlayOrder: String = {
    val stringList = mutable.MutableList[String]()
    var node = this
    while (node.parent.isDefined && node.card.isDefined) {
      if (node.trickWinner.isDefined) {
        stringList.+=(s"${node.trickWinner.get} Wins")
      }
      stringList.+=(s"${node.player.get} plays ${node.card.get}")
      node = node.parent.get
    }
    stringList.reverse.mkString("\n")
  }

  val children: ArrayBuffer[BridgeTrieNode] =
    new mutable.ArrayBuffer[BridgeTrieNode](13)

  override def compare(that: BridgeTrieNode): Int = {
    if (this.getNSTricksWon != that.getNSTricksWon) {
      this.getNSTricksWon.compare(that.getNSTricksWon)
    } else {
      this.getPlayOrder.mkString(",").compare(that.getPlayOrder.mkString(","))
    }
  }

  override def toString: String = {
    if (parent.isDefined) {
      s"Card $cardNumberPlayed: ${card.get} by ${player.get}"
    } else {
      "Root node"
    }
  }

  def getRoot: BridgeTrieNode = {
    var node: BridgeTrieNode = this
    while (node.parent.isDefined) {
      node = node.parent.get
    }
    node
  }

  def getLeaves: ArrayBuffer[BridgeTrieNode] = {
    def getChildren(someNode: BridgeTrieNode): ArrayBuffer[BridgeTrieNode] = {
      val results = new mutable.ArrayBuffer[BridgeTrieNode](1000)
      someNode.children.foreach(childNode => {
        if (childNode.children.nonEmpty) {
          results.++=(getChildren(childNode))
        } else {
          results.+=(childNode)
        }
      }
      )
      results
    }
    getChildren(this.getRoot)
  }

  @tailrec final def appendCardList(cardList: List[Card]): Unit = {
    if (cardList.nonEmpty) {
      val card = cardList.head
      var newNode: BridgeTrieNode = null
      if (!this.children.exists(t => t.card.isDefined && t.card.get == card)) {
        // We've not seen the next card as a child before
        newNode = new BridgeTrieNode(this.trumpSuit, Some(card), Some(this), Some(this.getNextPlayer))
        this.children.+=(newNode)
      } else {
        newNode = this.children.filter(t => t.card.isDefined && t.card.get == card).head
      }
      newNode.appendCardList(cardList.drop(1))
    }
  }

  var expectedTricksWon: Option[Int] = None

  var optimalLeaves: Option[Seq[BridgeTrieNode]] = None

  def optimizeTree(): Unit = {
    if (this.children.nonEmpty) {
      // Are we a non leaf?
      this.children.foreach(node => node.optimizeTree()) // Optimize every child
    }
    if (this.parent.isDefined) {
      //We're not root
      if (this.children.isEmpty) {
        // We're a leaf. The expected tricks won is whoever won this trick
        this.expectedTricksWon = if (this.trickWinner.get == this.player.get || this.trickWinner.get == Player.Partner(this.player.get)) Some(1) else Some(0)
      } else {
        val totalTricksLeft = math.ceil((this.getLeaves.head.cardNumberPlayed.toDouble - this.cardNumberPlayed) / 4).toInt
        // If we've optimized all the children
        val smallestNumberOfExpectedTricksWonInChildren = this.children.minBy(node => node.expectedTricksWon.get).expectedTricksWon.get
        this.optimalLeaves = Some(this.children.filter(node => node.expectedTricksWon.get == smallestNumberOfExpectedTricksWonInChildren))
        this.expectedTricksWon = Some(totalTricksLeft - smallestNumberOfExpectedTricksWonInChildren)

        // If we're the 4th in a trick, we know the winner. Add one to the expectedTricksWon if we won it
        if (this.trickWinner.isDefined && (this.trickWinner.get == this.player.get || this.trickWinner.get == Player.Partner(this.player.get))) {
          this.expectedTricksWon = Some(this.expectedTricksWon.get + 1)
        }
      }
    } else {
      //North is the first to play.
      this.expectedTricksWon = Some(this.children.maxBy(node => node.expectedTricksWon.get).expectedTricksWon.get)
      this.optimalLeaves = Some(this.children.filter(node => node.expectedTricksWon.get == this.expectedTricksWon.get))
    }
  }


  def BridgeTrieOrderingByExpectedTricksWon: Ordering[BridgeTrieNode] = Ordering.by(_.expectedTricksWon)


  /*def getBestPlay: BridgeTrieNode = {
    var bestLeadCard: BridgeTrieNode = null
    if (this.getNextPlayer == Player.North || this.getNextPlayer == Player.South) {
      bestLeadCard = this.getLeaves.reduce((cur, best) => {
        if (cur.getNSTricksWon > best.getNSTricksWon) {
          cur
        } else {
          best
        }
      })

    } else {
      bestLeadCard = this.getLeaves.reduce((cur, best) => {
        if (cur.getNSTricksWon < best.getNSTricksWon) {
          cur
        } else {
          best
        }
      })

    }
    while (bestLeadCard.parent.isDefined && bestLeadCard.parent.get.card.isDefined) {
      bestLeadCard=bestLeadCard.parent.get
    }
    bestLeadCard
  }*/
}
