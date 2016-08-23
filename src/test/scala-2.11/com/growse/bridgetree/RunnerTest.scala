package com.growse.bridgetree

import org.junit.{Rule, Test}
import org.junit.rules.ExpectedException

/**
  * Created by andrew on 21/08/2016.
  */
class RunnerTest {
  @Test
  def RunnerShouldWorkWithSuppliedDeck(): Unit = {
    Runner.main(Array[String]("--trumpsuit", "S", "--cards", "TwoH,AceS,ThreeD,JackC"))
  }

  @Test
  def RunnerShouldWorkWithSpecifiedHandCount(): Unit = {
    Runner.main(Array[String]("--trumpsuit", "S", "--trickcount", "3"))
  }

  val _exception = ExpectedException.none()

  @Rule
  def exception = _exception

  @Test
  def RunnerShouldFailWithSuppliedDeckNotHavingMultipleOfFourCards(): Unit = {
    exception.expect(classOf[IllegalArgumentException])
    Runner.main(Array[String]("--trumpsuit", "Blah", "--cards", "TwoH,AceS, ThreeD,JackC,AceH"))
  }
}
