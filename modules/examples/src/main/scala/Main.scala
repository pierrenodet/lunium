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

package lunium.examples

import cats.effect._
import lunium._
import lunium.umbreon._
import cats.data.EitherT
import cats.implicits._
import cats.effect.implicits._

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    UmbreonSession
      .fromCapabilities[IO]("localhost", "9515", Capabilities.lastchromemac)
      .use(session => {
        val res = for {
          url <- EitherT.fromEither[IO](Url("http://google.com"))
          _   <- session.to(url)
          _   <- session.deleteCookies()
          cookie <- EitherT.fromEither[IO](
                     Cookie("n", "a", "/", Some("google.com"), secure = false, httpOnly = false, scala.None)
                   )
          _     <- session.addCookie(cookie)
          found <- EitherT[IO, LuniumException, Cookie](session.findCookie("n").value)
        } yield found

        res
          .map(r => { println(r); ExitCode.Success })
          .valueOr(_ => ExitCode.Error)
      })

}
