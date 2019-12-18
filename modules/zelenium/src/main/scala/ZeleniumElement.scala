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

import lunium.selenium.implicits._
import org.openqa.selenium.{ OutputType => SeleniumOutputType, WebElement => SeleniumWebElement }
import zio._
import scala.jdk.CollectionConverters._

class ZeleniumElement(element: SeleniumWebElement) extends Element[IO] {

  def findElement(
    elementLocationStrategy: ElementLocationStrategy
  ): IO[Throwable, Option[Element[IO]]] =
    IO.effect(Option(element.findElement(elementLocationStrategy.asSelenium)).map(new ZeleniumElement(_)))

  def findElements(
    elementLocationStrategy: ElementLocationStrategy
  ): IO[Throwable, List[Element[IO]]] =
    IO.effect(
      element
        .findElements(elementLocationStrategy.asSelenium)
        .asScala
        .toList
        .map(new ZeleniumElement(_))
    )

  def screenshot: IO[Throwable, Array[Byte]] = IO.effect(element.getScreenshotAs(SeleniumOutputType.BYTES))

  def selected: IO[Throwable, Boolean] = IO.effect(element.isSelected)

  def attribute(name: String): IO[Throwable, String] = IO.effect(element.getAttribute(name))

  def hasAttribute(name: String): IO[Throwable, Boolean] =
    IO.effectSuspendTotal(attribute(name)).map(name => Option(name).isDefined)

  def property(name: String): IO[Throwable, String] = attribute(name)

  def css(name: String): IO[Throwable, String] = IO.effect(element.getCssValue(name))

  def text(whitespace: String = "WS"): IO[Throwable, String] = IO.effect(element.getText)

  def name: IO[Throwable, String] = IO.effect(element.getTagName)

  def rect: IO[Throwable, Rect] = IO.effect(element.getRect.asLunium)

  def enabled: IO[Throwable, Boolean] = IO.effect(element.isEnabled)

  def click: IO[Throwable, Unit] = IO.effect(element.click())

  def clear: IO[Throwable, Unit] = IO.effect(element.clear())

  def sendKeys(keys: Keys): IO[Throwable, Unit] = IO.effect(element.sendKeys(keys.asSelenium))

}

object ZeleniumElement {
  def apply(element: SeleniumWebElement): Element[IO] = new ZeleniumElement(element)
}
