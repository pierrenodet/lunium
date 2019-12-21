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
import cats.Bifunctor
import cats.effect.Sync
import lunium._
import cats.data.EitherT

/*
trait BakeryLaws[F[_, _]] {
  def algebra: Session[F]
  implicit def M: Monad[F[Throwable,*]]
  implicit def M2: Monad[F[Nothing,*]]
  implicit def BF: Bifunctor[F]

  import cats.syntax.apply._
  import cats.syntax.flatMap._
  import cats.syntax.functor._
  import cats.syntax.traverse._

  def addFind(cookie: Cookie) =
    cookie.domain.traverse(d => algebra.to(new Url(s"http://$d")).leftMap(_.asInstanceOf[Throwable])) >> algebra.addCookie(cookie).leftMap(_.asInstanceOf[Throwable]) >> algebra.findCookie(
      cookie.name
    ).leftMap(_.asInstanceOf[Throwable]) <-> M.pure(cookie)

  def addDeleteAddFind(cookie: Cookie) =
    cookie.domain.traverse(d => algebra.to(new Url(s"http://$d")).leftMap(_.asInstanceOf[Throwable])) >> algebra.addCookie(cookie).leftMap(_.asInstanceOf[Throwable]) >> algebra.deleteCookie(
      cookie.name
    ).leftMap(_.asInstanceOf[Throwable]) >> algebra.addCookie(cookie).leftMap(_.asInstanceOf[Throwable]) >> algebra.findCookie(cookie.name).leftMap(_.asInstanceOf[Throwable]) <-> (cookie.domain.traverse(
      d => algebra.to(new Url(s"http://$d")).leftMap(_.asInstanceOf[Throwable]))
    ).leftMap(_.asInstanceOf[Throwable]) >> algebra.addCookie(cookie).leftMap(_.asInstanceOf[Throwable]) >> algebra.findCookie(cookie.name).leftMap(_.asInstanceOf[Throwable])

  def deleteFind(cookie: Cookie) = {
    val err = for {
    _ <- algebra.deleteCookie(cookie.name)
    error <- algebra.findCookie(cookie.name)
  } yield(error)
    err  <-> M.pure(NoSuchElementException)
    }

}

object BakeryLaws {

  def apply[F[_, _]](instance: Session[F])(implicit ev: Monad[F[Throwable, *]]) =
    new BakeryLaws[F] {
      override val algebra                            = instance
      override implicit val M: Monad[F[Throwable, *]] = ev
    }

}
 */
