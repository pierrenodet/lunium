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

import lunium.selenium._
import zio._

class ZeleniumElement(private[lunium] val se: SeleniumElement) extends Element[IO] {

  def findElement(
    elementLocationStrategy: ElementLocationStrategy
  ): IO[SearchElementException, Element[IO]] =
    IO.fromEither(se.findElement(elementLocationStrategy).map(se => new ZeleniumElement(se)))

  def findElements(
    elementLocationStrategy: ElementLocationStrategy
  ): IO[SearchElementException, List[Element[IO]]] =
    IO.fromEither(
      se.findElements(elementLocationStrategy).map(_.map(se => new ZeleniumElement(se)))
    )

  def screenshot: IO[Nothing, Array[Byte]] = IO.fromEither(se.screenshot)

  def selected: IO[StaleElementReferenceException, Boolean] =
    IO.fromEither(se.selected)

  def attribute(name: String): IO[StaleElementReferenceException, Option[String]] =
    IO.fromEither(se.attribute(name))

  def hasAttribute(name: String): IO[StaleElementReferenceException, Boolean] =
    IO.fromEither(se.hasAttribute(name))

  def property(name: String): IO[StaleElementReferenceException, Option[String]] =
    IO.fromEither(se.property(name))

  def css(name: String): IO[StaleElementReferenceException, Option[String]] =
    IO.fromEither(se.css(name))

  def text(whitespace: String = "WS"): IO[StaleElementReferenceException, String] =
    IO.fromEither(se.text(whitespace))

  def name: IO[StaleElementReferenceException, String] =
    IO.fromEither(se.name)

  def rectangle: IO[StaleElementReferenceException, Rectangle] =
    IO.fromEither(se.rectangle)

  def enabled: IO[StaleElementReferenceException, Boolean] =
    IO.fromEither(se.enabled)

  def click: IO[InteractElementException, Unit] =
    IO.fromEither(se.click)

  def clear: IO[InteractElementException, Unit] =
    IO.fromEither(se.clear)

  def sendKeys(keys: Keys): IO[InteractElementException, Unit] =
    IO.fromEither(se.sendKeys(keys))

}
