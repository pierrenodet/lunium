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
import cats.effect.{ IO, Resource }
import lunium._
import lunium.umbreon._
import org.scalatest.funsuite.AnyFunSuite
import lunium.selenium.implicits._

class CookieSuite extends AnyFunSuite {

  val resource: Resource[IO, UmbreonSession[IO]] = UmbreonSession
    .headlessChrome[IO]

  test("trying to find a cookie that doesn't exist return NoSuchCookieException") {

    val err = resource
      .use(
        session =>
          (for {
            _   <- session.deleteCookies()
            res <- session.findCookie("kek")
          } yield res).value
      )
      .unsafeRunSync()

    assert(
      err
        .fold({ _: NoSuchCookieException =>
          true
        }, _ => false)
    )

  }

  test("trying to find a cookie that exist return the right cookie name and value") {

    val input = new Cookie("n", "a", "/", Some("google.com"), false, false, scala.None)

    val cookie = resource
      .use(
        session =>
          (for {
            url <- EitherT.fromEither[IO](Url("http://google.com")).leftMap(_.asInstanceOf[Nothing])
            _   <- session.to(url).leftMap(_.asInstanceOf[Nothing])
            _   <- session.deleteCookies()
            _   <- session.addCookie(input).leftMap(_.asInstanceOf[Nothing])
            res <- session.findCookie(input.name)
          } yield res).value
      )
      .unsafeRunSync()

    assert(
      cookie.exists(res => (res.name == input.name) && (res.value == input.value))
    )

  }

  test("trying to add a cookie on a wrong domain return InvalidCookieDomainException") {

    val err = resource.use(
      session =>
        (for {
          cookie <- EitherT
                     .fromEither[IO](
                       Cookie("n", "a", "/", Some("kek.kek"), secure = false, httpOnly = false, scala.None)
                     )
                     .leftMap(_.asInstanceOf[Nothing])
          res <- session.addCookie(cookie)
        } yield res).value
    )

    assert(
      err
        .unsafeRunSync()
        .fold({ _: InvalidCookieDomainException =>
          true
        }, _ => false)
    )

  }

  test("get cookies while they are none should be ok") {

    val empty = resource.use(session => {
      session.deleteCookies()
      session.cookies.value
    })

    assert(empty.unsafeRunSync().exists(_.isEmpty))

  }

  test("cookie conversions should be invariant") {

    val inputs = List(
      new Cookie("n", "a", "/", Some("google.com"), false, false, scala.None),
      new Cookie("n", "a", "/", scala.None, true, false, scala.None)
    )

    assert(inputs.forall(a => a == a.asSelenium.asLunium))

  }

}
