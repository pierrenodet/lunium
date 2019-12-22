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

import cats.effect.{ IO, Resource }
import lunium._
import lunium.umbreon._
import org.scalatest.funsuite.AnyFunSuite

class NavigationSuite extends AnyFunSuite {

  val resource: Resource[IO, UmbreonSession[IO]] = UmbreonSession
    .headlessChrome[IO]
    
  val googleUrl = new Url("https://www.google.com")

  test("find non existing element return lunium.NoSuchElement") {

    val err = resource.use(
      session =>
        (for {
          _     <- session.to(googleUrl).value
          error <- session.findElement(CSS(".ban.hot")).value
        } yield (error))
    )

    assert(
      err
        .unsafeRunSync()
        .fold({
          case exception: InvalidSelectorException => false
          case exception: NoSuchElementException   => true
        }, _ => false)
    )

  }

  test("wrong element location strategy return lunium.InvalidSelectorException") {

    val err = resource.use(
      session =>
        (for {
          _     <- session.to(googleUrl).value
          error <- session.findElement(CSS("423432")).value
        } yield (error))
    )

    assert(
      err
        .unsafeRunSync()
        .fold({
          case exception: InvalidSelectorException => true
          case exception: NoSuchElementException   => false
        }, _ => false)
    )

  }

  test("doing back without history should not crash") {

    val fine = resource.use(
      session =>
        (for {
          _ <- session.to(googleUrl)
          _ <- session.back
          _ <- session.back
          _ <- session.back
        } yield ()).value
    )

    assert(
      fine
        .unsafeRunSync()
        .fold({
          case _: InsecureCertificateException => false
          case _: TimeoutException             => false
          case _: HttpUnauthorizedException    => false
        }, a => true)
    )

  }

  test("going to a website should return the website url used when using url") {

    val url = resource.use(
      session =>
        (for {
          _   <- session.to(googleUrl).value
          url <- session.url.value
        } yield (url))
    )

    assert(url.unsafeRunSync().fold(_ => false, res => res.startsWith(googleUrl.value)))

  }

}
