package com.growse.bridgetree


import com.growse.bridgetree.Player.Player
import com.growse.bridgetree.Suit.Suit
import com.typesafe.scalalogging.LazyLogging
import org.slf4j.MarkerFactory

import scala.annotation.tailrec
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * Created by andrew on 27/08/2016.
  */
class BridgeTrieNode(val trumpSuit: Suit = null, val card: Option[Card] = None, val parent: Option[BridgeTrieNode] = None, val player: Option[Player] = None) extends Ordered[BridgeTrieNode] with LazyLogging {

  val cardNumberPlayed = this.getCardNumberPlayed
  var trickWinner: Option[Player] = None
  if (cardNumberPlayed % 4 == 0) {
    trickWinner = this.getLastTrickWinner
  }


  def getNSTricksWon: Int = {
    var node = this
    var counter = 0
    while (node.parent.isDefined) {
      if (node.getThisUnfinishedTrick.isEmpty) {
        if (node.trickWinner.isDefined && (node.trickWinner.get == Player.North || node.trickWinner.get == Player.South)) {
          counter += 1
        }
      }
      node = node.parent.get
    }
    counter
  }

  def getNextPlayer: Player = {
    if (getThisUnfinishedTrick.isEmpty && this.parent.isDefined) {
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

  def getThisUnfinishedTrick: Option[List[BridgeTrieNode]] = {
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

  var optimalChildren: Option[Seq[BridgeTrieNode]] = None

  def optimalLeaf:BridgeTrieNode={
    var node=this
    while (node.optimalChildren.isDefined) {
      node=node.optimalChildren.get.head
    }
    node
  }

  def optimizeTree(): Unit = {
    if (this.expectedTricksWon.isDefined) {
      return
    }
    if (this.children.nonEmpty) {
      // Are we a non leaf?
      logger.debug(s"${this} is Not a leaf node, optimizing children")
      this.children.foreach(node => node.optimizeTree()) // Optimize every child
    }
    logger.debug(s"Card: ${this.cardNumberPlayed} - ${this.player} - ${this.card}")
    if (this.parent.isDefined) {
      //We're not root
      if (this.children.isEmpty) {

        // We're a leaf. The expected tricks won is whoever won this trick
        this.expectedTricksWon = if (this.trickWinner.get == this.player.get || this.trickWinner.get == Player.Partner(this.player.get)) Some(1) else Some(0)
        logger.debug(s"We're a leaf node. ${this.player.get} played ${this.card.get} and can expect to win $expectedTricksWon. This trick was won by ${this.getLastTrickWinner.get}. ${this.getLastTrick.get}")
      } else {
        val totalTricksLeft = math.ceil((this.getLeaves.head.cardNumberPlayed.toDouble - this.cardNumberPlayed) / 4).toInt
        // If we've optimized all the children
        val largestNumberOfExpectedTricksWonInChildren = this.children.maxBy(node => node.expectedTricksWon.get).expectedTricksWon.get
        this.optimalChildren = Some(this.children.filter(node => node.expectedTricksWon.get == largestNumberOfExpectedTricksWonInChildren))
        this.expectedTricksWon = Some(totalTricksLeft - largestNumberOfExpectedTricksWonInChildren)
        logger.debug(s"The next player is ${this.children.head.player} and can play ${this.children.size} cards. Best result for them is to win $largestNumberOfExpectedTricksWonInChildren")
        logger.debug(s"${this.player.get} played ${this.card.get}. Can expect to win $expectedTricksWon.")

        // If we're the 4th in a trick, we know the winner. Add one to the expectedTricksWon if we won it
        if (this.trickWinner.isDefined) {
          logger.debug(s"Fourth card, trick won by ${this.getLastTrickWinner.get}: ${this.getLastTrick.get}")
        }
        if (this.trickWinner.isDefined && (this.trickWinner.get == this.player.get || this.trickWinner.get == Player.Partner(this.player.get))) {
          //logger.debug(s"Adding one to expected total as 4th card partnership won the trick")
          //this.expectedTricksWon = Some(this.expectedTricksWon.get + 1)
        }
      }
    } else {
      //North is the first to play.
      this.expectedTricksWon = Some(this.children.maxBy(node => node.expectedTricksWon.get).expectedTricksWon.get)
      this.optimalChildren = Some(this.children.filter(node => node.expectedTricksWon.get == this.expectedTricksWon.get))
    }
  }


  def BridgeTrieOrderingByExpectedTricksWon: Ordering[BridgeTrieNode] = Ordering.by(_.expectedTricksWon)
}
