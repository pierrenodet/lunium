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

package lunium.laws

import cats.kernel.laws._
import cats.implicits._
import cats.Monad

import lunium._

trait BakeryLaws[F[_]] {
  def algebra: Session[F]
  implicit def M: Monad[F]

  import cats.syntax.apply._
  import cats.syntax.flatMap._
  import cats.syntax.functor._
  import cats.syntax.traverse._

  def addFind(cookie: Cookie) =
    cookie.domain.traverse(d => algebra.to(s"http://$d")) >> algebra.addCookie(cookie) >> algebra.findCookie(
      cookie.name
    ) <-> M.pure(Some(cookie))

  def addDeleteAddFind(cookie: Cookie) =
    cookie.domain.traverse(d => algebra.to(s"http://$d")) >> algebra.addCookie(cookie) >> algebra.deleteCookie(
      cookie.name
    ) >> algebra.addCookie(cookie) >> algebra.findCookie(cookie.name) <-> (cookie.domain.traverse(
      d => algebra.to(s"http://$d")
    ) >> algebra.addCookie(cookie) >> algebra.findCookie(cookie.name))

  def deleteFind(cookie: Cookie) =
    algebra.deleteCookie(cookie.name) >> algebra.findCookie(cookie.name) <-> M.pure(scala.None)

  //can't add cookie not on domain
}

object BakeryLaws {

  def apply[F[_]](instance: Session[F])(implicit ev: Monad[F]) =
    new BakeryLaws[F] {
      override val algebra              = instance
      override implicit val M: Monad[F] = ev
    }

}
