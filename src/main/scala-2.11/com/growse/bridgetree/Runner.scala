package com.growse.bridgetree

import ch.qos.logback.classic.{Level, Logger}
import com.growse.bridgetree.Suit.Suit
import com.typesafe.scalalogging.LazyLogging
import org.apache.commons.lang3.time.StopWatch
import org.rogach.scallop.ArgType.V
import org.rogach.scallop.{ScallopConf, ScallopOption, ValueConverter}
import org.slf4j.LoggerFactory

import scala.collection.mutable

class Conf(arguments: Seq[String]) extends ScallopConf(arguments) {
  val suitValidator = new ValueConverter[Suit] {
    override def parse(s: List[(String, List[String])]): Either[String, Option[Suit]] =
      s match {
        case (_, "S" :: Nil) :: Nil => Right(Some(Suit.S))
        case (_, "D" :: Nil) :: Nil => Right(Some(Suit.D))
        case (_, "H" :: Nil) :: Nil => Right(Some(Suit.H))
        case (_, "C" :: Nil) :: Nil => Right(Some(Suit.C))
        case _ => Right(Some(null))
      }

    override val tag: _root_.scala.reflect.runtime.universe.TypeTag[Suit] = scala.reflect.runtime.universe.typeTag[Suit]
    override val argType: V = org.rogach.scallop.ArgType.SINGLE
  }
  val trumpsuit = opt[Suit](required = true)(suitValidator)
  val trickcount = opt[Int]()
  val cards = opt[String]()
  val verbose = opt[Boolean]()
  mutuallyExclusive(trickcount, cards)
  verify()
}


/**
  * Created by andrew on 21/08/2016.
  */
object Runner extends LazyLogging {

  def main(args: Array[String]): Unit = {
    val conf = new Conf(args)

    if (conf.verbose.getOrElse(false)) {
      LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME)
        .asInstanceOf[Logger].setLevel(Level.DEBUG)
    } else {
      LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME)
        .asInstanceOf[Logger].setLevel(Level.INFO)
    }

    val cards: mutable.LinkedHashSet[Card] = getCards(conf)
    val cardsPerHand = cards.size / 4

    logger.info(s"Initial Deck is $cards")
    logger.info(s"Trick count: $cardsPerHand")
    logger.info(s"Trumps are ${conf.trumpsuit.getOrElse(null)}")

    val bridgePlayer = new BridgePlayer(cards, conf.trumpsuit.getOrElse(null))

    logger.info(s"Hands: ${bridgePlayer.generateHandsFromDeck(cards)}")

    val stopWatch = new StopWatch()
    stopWatch.start()
    bridgePlayer.Play()
    stopWatch.stop()

    logger.info(s"Worst result for lead: ${bridgePlayer.rootTrieNode.getLeaves.head.toString}")
    logger.info(s"Best result for lead: ${bridgePlayer.rootTrieNode.getLeaves.last.toString}")

    logger.info(s"Legal play count: ${bridgePlayer.rootTrieNode.getLeaves.size}")
    logger.info(s"Done in $stopWatch")
  }

  def getCards(conf: Conf): mutable.LinkedHashSet[Card] = {
    val cards: mutable.LinkedHashSet[Card] = new scala.collection.mutable.LinkedHashSet()
    if (conf.trickcount.isDefined) {
      cards.++=(new Deck().shuffle.deal(4 * conf.trickcount.getOrElse(0))._1)
    } else if (conf.cards.isDefined) {
      cards.++=(conf.cards.getOrElse("").split(",").map(cardstring => Card.parse(cardstring.trim)))
      if (cards.size % 4 != 0) {
        throw new IllegalArgumentException("Supplied deck must have multiple of 4 cards in it")
      }
    }
    else {
      // The whole deck! This may take a while
      cards.++=(new Deck().shuffle.cards)
    }
    cards
  }
}
