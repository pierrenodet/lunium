/*
 * Copyright 2020 Pierre Nodet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package lunium.tests

import cats.data.EitherT
import cats.effect._
import lunium._
import lunium.umbreon._
import org.scalatest.funsuite.AnyFunSuite
import lunium.selenium.implicits._
import cats.implicits._
class ElementSuite extends AnyFunSuite {

  val resource: Resource[IO, UmbreonSession[IO]] = UmbreonSession
    .fromCapabilities[IO]("localhost", "9515", Capabilities.lastchromemac)

  val googleUrl    = new Url("https://www.google.com")
  val microsoftUrl = new Url("https://www.microsoft.com")

  test(
    "try to get something out of a element from a previous page should return a lunium.StaleElementReferenceException"
  ) {

    val err = resource.use(
      session =>
        (for {
          _       <- session.to(googleUrl).leftWiden[LuniumException]
          element <- session.findElement(XPath("""//*[@id="hplogo"]""")).leftWiden[LuniumException]
          _       <- session.to(microsoftUrl).leftWiden[LuniumException]
          res     <- element.attribute("src").leftWiden[LuniumException]
        } yield (res)).value
    )

    assert(
      err
        .unsafeRunSync()
        .fold(_ match {
          case _: StaleElementReferenceException => true
          case _: LuniumException                => false
        }, _ => false)
    )

  }

  test(
    "try to get a property something out of a element that doesn't exist should return a lunium.NullPointerException"
  ) {

    val err = resource.use(
      session =>
        (for {
          _       <- session.to(googleUrl).leftWiden[LuniumException]
          element <- session.findElement(XPath("""//*[@id="hplogo"]""")).leftWiden[LuniumException]
          res     <- element.attribute("dsqdqsd").leftWiden[LuniumException]
        } yield (res)).value
    )

    assert(
      err
        .unsafeRunSync()
        .fold(_ match {
          case _: NoSuchAttributeException => true
          case _: LuniumException          => false
        }, _ => false)
    )

  }

  test(
    "type bonjour in google search"
  ) {

    val input = "bonjour"

    val bonjour = resource.use(
      session =>
        (for {
          _ <- session.to(googleUrl).leftWiden[LuniumException]
          element <- session
                      .findElement(XPath("""//*[@id="tsf"]/div[2]/div[1]/div[1]/div/div[2]/input"""))
                      .leftWiden[LuniumException]
          //_     <- element.sendKeys(Keys(Set("b","o","n","j","o","u","r"))).leftWiden[LuniumException]
          _   <- element.sendKeys(Keys(Set(input))).leftWiden[LuniumException]
          res <- element.attribute("value").leftWiden[LuniumException]
        } yield (res)).value
    )

    assert(
      bonjour
        .unsafeRunSync()
        .fold(_ => false, b => b == input)
    )

  }
}
