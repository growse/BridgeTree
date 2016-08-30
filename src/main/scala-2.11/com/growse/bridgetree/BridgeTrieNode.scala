package com.growse.bridgetree


import com.growse.bridgetree.Player.Player

import scala.collection.mutable

/**
  * Created by andrew on 27/08/2016.
  */
class BridgeTrieNode(val card: Option[Card] = None, val parent: Option[BridgeTrieNode] = None, val player: Option[Player] = None) {

  val children: mutable.HashSet[BridgeTrieNode] =
    new mutable.HashSet[BridgeTrieNode]()
}
