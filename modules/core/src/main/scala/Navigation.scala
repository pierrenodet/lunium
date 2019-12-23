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

package lunium

import java.net.MalformedURLException

sealed trait PageLoadStrategy
case object None   extends PageLoadStrategy
case object Eager  extends PageLoadStrategy
case object Normal extends PageLoadStrategy

sealed trait NavigationCommand
case object Back    extends NavigationCommand
case object Forward extends NavigationCommand
case object Refresh extends NavigationCommand

sealed case class Url private[lunium] (value: String) extends NavigationCommand

object Url {

  def apply(value: String): Either[InvalidArgumentException, Url] =
    try {
      Right(new Url(new java.net.URL(value).toString))
    } catch {
      case e: MalformedURLException => Left(new InvalidArgumentException(e.getMessage))
    }

}

trait Navigate[F[_, _]] {

  def navigate(command: NavigationCommand): F[NavigationException, Unit]
  def to(url: Url): F[NavigationException, Unit] = navigate(url)
  def back: F[NavigationException, Unit]         = navigate(Back)
  def forward: F[NavigationException, Unit]      = navigate(Forward)
  def refresh: F[NavigationException, Unit]      = navigate(Refresh)

  def url: F[Nothing, Url]
  def title: F[Nothing, String]

}

object Navigate {

  def apply[F[_, _]](implicit instance: Navigate[F]): Navigate[F] = instance

}
