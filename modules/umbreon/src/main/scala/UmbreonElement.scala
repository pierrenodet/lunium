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

package lunium.umbreon

import cats.data.EitherT
import cats.effect._
import lunium._
import lunium.selenium._

class UmbreonElement[F[_]: Sync](private[lunium] val se: SeleniumElement) extends Element[EitherT[F, *, *]] {

  def findElement(
    elementLocationStrategy: ElementLocationStrategy
  ): EitherT[F, SearchElementException, Element[EitherT[F, *, *]]] =
    EitherT.fromEither(se.findElement(elementLocationStrategy).map(se => new UmbreonElement[F](se)))

  def findElements(
    elementLocationStrategy: ElementLocationStrategy
  ): EitherT[F, SearchElementException, List[Element[EitherT[F, *, *]]]] =
    EitherT.fromEither(
      se.findElements(elementLocationStrategy).map(_.map(se => new UmbreonElement[F](se)))
    )

  def screenshot: EitherT[F, Nothing, Array[Byte]] = EitherT.fromEither(se.screenshot)

  def selected: EitherT[F, StaleElementReferenceException, Boolean] =
    EitherT.fromEither(se.selected)

  def attribute(name: String): EitherT[F, StaleElementReferenceException, Option[String]] =
    EitherT.fromEither(se.attribute(name))

  def hasAttribute(name: String): EitherT[F, StaleElementReferenceException, Boolean] =
    EitherT.fromEither(se.hasAttribute(name))

  def property(name: String): EitherT[F, StaleElementReferenceException, Option[String]] =
    EitherT.fromEither(se.property(name))

  def css(name: String): EitherT[F, StaleElementReferenceException, Option[String]] =
    EitherT.fromEither(se.css(name))

  def text(whitespace: String = "WS"): EitherT[F, StaleElementReferenceException, String] =
    EitherT.fromEither(se.text(whitespace))

  def name: EitherT[F, StaleElementReferenceException, String] =
    EitherT.fromEither(se.name)

  def rectangle: EitherT[F, StaleElementReferenceException, Rectangle] =
    EitherT.fromEither(se.rectangle)

  def enabled: EitherT[F, StaleElementReferenceException, Boolean] =
    EitherT.fromEither(se.enabled)

  def click: EitherT[F, InteractElementException, Unit] =
    EitherT.fromEither(se.click)

  def clear: EitherT[F, InteractElementException, Unit] =
    EitherT.fromEither(se.clear)

  def sendKeys(keys: Keys): EitherT[F, InteractElementException, Unit] =
    EitherT.fromEither(se.sendKeys(keys))

}
