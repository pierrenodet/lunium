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
import cats.implicits._
import lunium._
import lunium.umbreon._
import org.scalatest.funsuite.AnyFunSuite

class ElementSuite extends AnyFunSuite {

  val resource: Resource[IO, UmbreonSession[IO]] = UmbreonSession
    .headlessChrome[IO]

  val googleUrl    = new Url("https://www.google.com")
  val microsoftUrl = new Url("https://www.microsoft.com")

  test(
    "try to get something out of a element from a previous page should return a StaleElementReferenceException"
  ) {

    val err = resource
      .use(
        session =>
          (for {
            _       <- session.to(googleUrl)
            element <- session.findElement(XPath("""//*[@id="hplogo"]"""))
            _       <- session.to(microsoftUrl)
            res     <- EitherT[IO, LuniumException, Option[String]](element.attribute("src").value)
          } yield res).value
      )
      .unsafeRunSync()

    assert(
      err.fold(_ match {
        case _: StaleElementReferenceException => true
        case _: LuniumException                => false
      }, _ => false)
    )

  }

  test(
    "try to get a property something out of a element that doesn't exist should return a None"
  ) {

    val err = resource
      .use(
        session =>
          (for {
            _       <- session.to(googleUrl)
            element <- session.findElement(XPath("""//*[@id="hplogo"]"""))
            res     <- EitherT[IO, LuniumException, Option[String]](element.attribute("dsqdqsd").value)
          } yield res).value
      )
      .unsafeRunSync()

    assert(err.exists(_.isEmpty))

  }

  test(
    "type bonjour in google search"
  ) {

    val input = "bonjour"

    val bonjour = resource
      .use(
        session =>
          (for {
            _ <- session.to(googleUrl)
            element <- session
                        .findElement(XPath("""//*[@id="tsf"]/div[2]/div[1]/div[1]/div/div[2]/input"""))
            _   <- element.sendKeys(Keys(Set(input)))
            res <- EitherT[IO, LuniumException, Option[String]](element.attribute("value").value)
          } yield res).value
      )
      .unsafeRunSync()

    assert(bonjour.exists(_.contains(input)))

  }
}
