package com.growse.bridgetree

import ch.qos.logback.classic.Level
import com.growse.bridgetree.Suit.Suit
import com.typesafe.scalalogging.LazyLogging
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.StopWatch
import org.rogach.scallop.{ScallopConf, ScallopOption, ValueConverter}
import org.slf4j.LoggerFactory


/**
  * Created by andrew on 21/08/2016.
  */
object Runner extends LazyLogging {

  def main(args: Array[String]): Unit = {
    LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME)
      .asInstanceOf[ch.qos.logback.classic.Logger].setLevel(Level.INFO)

    val conf = new Conf(args)

    logger.info(s"FECK ${conf.trickscount()}")
    // See if we've been given a list of cards, or a number for how many tricks to play
    val cards: scala.collection.mutable.LinkedHashSet[Card] = new scala.collection.mutable.LinkedHashSet()
    if (args.length > 1 && args(1) != "") {
      if (StringUtils.isNumeric(args(1))) {
        cards.++=(new Deck().shuffle.deal(4 * Integer.parseInt(args(1)))._1)
      } else {
        cards.++=(args(1).split(",").map(cardstring => Card.parse(cardstring.trim)))
        if (cards.size % 4 != 0) {
          throw new IllegalArgumentException("Supplied deck must have multiple of 4 cards in it")
        }
      }
    } else {
      // The whole deck! This may take a while
      cards.++=(new Deck().shuffle.cards)
    }

    val cardsPerHand = cards.size / 4
    logger.info(s"Initial Deck is $cards")

    logger.info(s"Trick count: $cardsPerHand")
    logger.info(s"Trumps are ${conf.trumpsuit}")

    val bridgeTree = new BridgeTree(cards, conf.trumpsuit.getOrElse(null))

    logger.info(s"${bridgeTree.generateHandsFromDeck(cards)}")

    val stopWatch = new StopWatch()
    stopWatch.start()
    bridgeTree.Play()
    stopWatch.stop()

    logger.info(s"Worst result for lead: ${bridgeTree.ResultsCounter.ways.head}")
    logger.info(s"Best result for lead: ${bridgeTree.ResultsCounter.ways.last}")
    logger.info(s"Total legal plays: ${bridgeTree.ResultsCounter.ways.size}")
    logger.info(s"Done in $stopWatch")
  }

  class Conf(arguments: Seq[String]) extends ScallopConf(arguments) {
    val suitConverter = new ValueConverter[Suit] {
      override def parse(s: List[(String, List[String])]): Either[String, Option[Suit]] = s match {
        case _ => null
      }

      override val argType = org.rogach.scallop.ArgType.SINGLE
      override val tag: _root_.scala.reflect.runtime.universe.TypeTag[Suit] = null
    }
    // all options that are applicable to builder (like description, default, etc)
    // are applicable here as well
    val trumpsuit: ScallopOption[Suit] = opt[Suit]("trumpsuit", descr = "The Trump Suit", required = true)(suitConverter)
    val trickscount: ScallopOption[Int] = opt[Int]("trickscount")
    val cards: ScallopOption[List[String]] = opt[List[String]]("cards")
    mutuallyExclusive(trickscount, cards)
    verify()
  }

}
