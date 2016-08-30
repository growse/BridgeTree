package com.growse.bridgetree

import org.junit.Assert._

import org.junit.Test

/**
  * Created by andrew on 30/08/2016.
  */
class BridgeTrieNodeTest {
  @Test
  def TrieNodeShouldRememberWhatIPutInItAndStuff(): Unit = {
    val rootTrieNode = new BridgeTrieNode()
    val firstChild = new BridgeTrieNode(Some(Card(Suit.S, Pip.Ace)), Some(rootTrieNode), Some(Player.North))
    rootTrieNode.children.+=(firstChild)
    assertEquals(1, rootTrieNode.children.size) // Do we have one child?
    assertEquals(None, firstChild.parent.get.card) // Is the parent of the first child the root node?
    assertEquals(Player.North, rootTrieNode.children.head.player.get) //Is the first child of the root node a play made by North?
  }

}
