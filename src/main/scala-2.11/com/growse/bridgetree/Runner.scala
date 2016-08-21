package com.growse.bridgetree

import ch.qos.logback.classic.Level
import com.growse.bridgetree.Suit.Suit
import com.typesafe.scalalogging.LazyLogging
import org.apache.commons.lang3.time.StopWatch
import org.slf4j.LoggerFactory


/**
  * Created by andrew on 21/08/2016.
  */
object Runner extends LazyLogging {

  def main(args: Array[String]): Unit = {
    LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME)
      .asInstanceOf[ch.qos.logback.classic.Logger].setLevel(Level.INFO)
    var deck: Deck = null
    var trumpSuit: Suit = null
    if (args.length > 0) {
      try {
        trumpSuit = Suit.withName(args(0))
      } catch {
        case e: NoSuchElementException =>
      }
    }
    if (args.length > 1 && args(1) != "") {
      val cards = args(1).split(",").map(cardstring => Card.parse(cardstring.trim)).toList
      if (cards.size % 4 != 0) {
        throw new IllegalArgumentException("Supplied deck must have multiple of 4 cards in it")
      }
      deck = new Deck(cards)
    } else {
      deck = new Deck().shuffle
    }

    val cardsPerHand = deck.cards.size / 4
    logger.info(s"Initial Deck is $deck")
    logger.info(s"Trick count: $cardsPerHand")
    logger.info(s"Trumps are ${if (trumpSuit != null) trumpSuit else "No Trumps"}")

    val bridgeTree = new BridgeTree(deck, cardsPerHand = cardsPerHand, trumpSuit)

    val stopWatch = new StopWatch()
    stopWatch.start()
    bridgeTree.Play()
    stopWatch.stop()

    logger.info(s"Worst result for lead: ${bridgeTree.ResultsCounter.minWay}")
    logger.info(s"Best result for lead: ${bridgeTree.ResultsCounter.maxWay}")
    logger.info(s"Done in $stopWatch")
  }

}
