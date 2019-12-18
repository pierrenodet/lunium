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

package lunium.laws.arbitrary

import cats.kernel._
import lunium._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen._
import org.scalacheck._

trait ArbitraryCookie {
  final val CookieGen: Gen[Cookie] = for {
    name     <- nonEmptyListOf(alphaLowerChar).map(_.mkString)
    value    <- nonEmptyListOf(alphaLowerChar).map(_.mkString)
    path     <- nonEmptyListOf(nonEmptyListOf(alphaLowerChar)).map(_.map(_.mkString)).map(_.foldLeft("")(_ + "/" + _))
    domain   <- nonEmptyListOf(alphaLowerChar).map(_.mkString)
    secure   <- arbitrary[Boolean]
    httpOnly <- arbitrary[Boolean]
    expiry   <- posNum[Long]
  } yield Cookie(name, value, "/", Some("google.com"), true, httpOnly, scala.None)

  implicit final val ArbitraryCookie: Arbitrary[Cookie] = Arbitrary(CookieGen)

  implicit val EqCookie: Eq[Cookie] = Eq.fromUniversalEquals
}
