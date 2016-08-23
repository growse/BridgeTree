package com.growse.bridgetree

import ch.qos.logback.classic.Level
import com.growse.bridgetree.Suit.Suit
import com.typesafe.scalalogging.LazyLogging
import org.apache.commons.lang3.time.StopWatch
import org.rogach.scallop.ArgType.V
import org.rogach.scallop.{ScallopConf, ValueConverter}
import org.slf4j.LoggerFactory

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
        .asInstanceOf[ch.qos.logback.classic.Logger].setLevel(Level.DEBUG)
    } else {
      LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME)
        .asInstanceOf[ch.qos.logback.classic.Logger].setLevel(Level.INFO)
    }

    val cards: scala.collection.mutable.LinkedHashSet[Card] = new scala.collection.mutable.LinkedHashSet()
    if (args.length > 1 && args(1) != "") {
      if (conf.trickcount.isDefined) {
        cards.++=(new Deck().shuffle.deal(4 * conf.trickcount.getOrElse(0))._1)
      } else {
        cards.++=(conf.cards.getOrElse("").split(",").map(cardstring => Card.parse(cardstring.trim)))
        if (cards.size % 4 != 0) {
          throw new IllegalArgumentException("Supplied deck must have multiple of 4 cards in it")
        }
      }
    } else {
      cards.++=(new Deck().shuffle.cards)
    }

    val cardsPerHand = cards.size / 4
    logger.info(s"Initial Deck is $cards")
    logger.info(s"Trick count: $cardsPerHand")
    logger.info(s"Trumps are ${conf.trumpsuit.getOrElse(null)}")

    val bridgeTree = new BridgeTree(cards, conf.trumpsuit.getOrElse(null))

    val stopWatch = new StopWatch()
    stopWatch.start()
    bridgeTree.Play()
    stopWatch.stop()

    logger.info(s"Worst result for lead: ${bridgeTree.ResultsCounter.ways.head}")
    logger.info(s"Best result for lead: ${bridgeTree.ResultsCounter.ways.last}")
    logger.info(s"Legal play count: ${bridgeTree.ResultsCounter.ways.size}")
    logger.info(s"Done in $stopWatch")
  }

}
