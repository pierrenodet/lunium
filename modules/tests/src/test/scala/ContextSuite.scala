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
import cats.implicits._

class ContextSuite extends AnyFunSuite {

  val resource: Resource[IO, UmbreonSession[IO]] = UmbreonSession
    .headlessChrome[IO]

  test("context of new session is default") {

    val err = resource
      .use(
        session =>
          (for {
            res <- session.current
          } yield res).value
      )
      .unsafeRunSync()

    assert(err.exists(_ match {
      case Parent       => false
      case Window(_, _) => true
      case Frame(_)     => false
      case Default      => false
    }))

  }

  /*
  test("minimize maximize") {

    val res = resource.use(
      session =>
        (for {
          _     <- session.maximize
          rect1 <- session.rect.leftMap(_.asInstanceOf[LuniumException])
          _     <- session.minimize
          _     <- session.maximize
          rect2 <- session.rect.leftMap(_.asInstanceOf[LuniumException])
        } yield (rect1, rect2)).value
    )

    assert(res.map(e => e.exists { case (rect1, rect2) => rect1 == rect2 }).unsafeRunSync())

  }

  test("set size") {

    val input = Rect(40, 40, 500, 500).right.get

    val res = resource.use(
      session =>
        (for {
          _    <- session.resize(input)
          rect <- session.rect.leftMap(_.asInstanceOf[LuniumException])
        } yield (rect)).value
    )

    assert(res.map(e => e.exists(rect => rect == input)).unsafeRunSync())

  }
 */
}
