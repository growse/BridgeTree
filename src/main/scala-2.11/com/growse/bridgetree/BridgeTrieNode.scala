package com.growse.bridgetree


import com.growse.bridgetree.Player.Player
import com.growse.bridgetree.Suit.Suit

import scala.annotation.tailrec
import scala.collection.mutable

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

  val children: mutable.TreeSet[BridgeTrieNode] =
    new mutable.TreeSet[BridgeTrieNode]()

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

  def getLeaves: mutable.TreeSet[BridgeTrieNode] = {
    def getChildren(someNode: BridgeTrieNode): mutable.TreeSet[BridgeTrieNode] = {
      val results = new mutable.TreeSet[BridgeTrieNode]()
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

  def getMinMaxNSTricksWon: (Int, Int) = {
    val mapped = getLeaves
      .map(node => node.getNSTricksWon)
    (mapped.min, mapped.max)

  }

  def getBestPlay: BridgeTrieNode = {//TODO return bottom card and play
    var ret: BridgeTrieNode = null
    if (this.getNextPlayer == Player.North || this.getNextPlayer == Player.South) {
      ret = this.getLeaves.reduce((cur, best) => {
        if (cur.getNSTricksWon > best.getNSTricksWon) {
          cur
        } else {
          best
        }
      })

    } else {
      ret = this.getLeaves.reduce((cur, best) => {
        if (cur.getNSTricksWon < best.getNSTricksWon) {
          cur
        } else {
          best
        }
      })

    }
    while (ret.parent.get != this) {
      ret = ret.parent.get
    }
    ret
  }
}
