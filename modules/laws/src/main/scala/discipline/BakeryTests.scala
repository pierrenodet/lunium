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

package lunium.laws.discipline

import cats.Monad
import cats.kernel.laws._
import org.typelevel.discipline.Laws
import cats.kernel.laws.discipline._
import cats.{ Eq, Monad }
import org.scalacheck.Arbitrary
import org.scalacheck.Prop._
import org.scalacheck._
import cats._
import cats.data.IdT
import cats.effect.implicits._
import cats.effect._
import cats.effect.laws._
import lunium._
import lunium.laws._
import lunium.laws.arbitrary._
import lunium.umbreon._

trait BakeryTests[F[_]] extends Laws {
  def laws: BakeryLaws[F]

  def algebra(implicit arbEmail: Arbitrary[Cookie], eqFBool: Eq[F[Boolean]], eqFOptId: Eq[F[Option[Cookie]]]) =
    new SimpleRuleSet(
      name = "Bakery",
      "find and add is some"                            -> forAll(laws.addFind _),
      "find and delete is none"                         -> forAll(laws.deleteFind _),
      "find and add and delete and add is find and add" -> forAll(laws.addDeleteAddFind _)
    )
}

object BakeryTests {

  def apply[F[_]: Monad](instance: Session[F]) = new BakeryTests[F] {
    override val laws: BakeryLaws[F] = BakeryLaws(instance)
  }

}
