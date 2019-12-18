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

import cats.effect._
import lunium._
import lunium.selenium.implicits._
import org.openqa.selenium.{ OutputType => SeleniumOutputType, WebElement => SeleniumWebElement }
import scala.jdk.CollectionConverters._

class UmbreonElement[F[_]: Sync](private[lunium] val element: SeleniumWebElement) extends Element[F] {

  def findElement(
    elementLocationStrategy: ElementLocationStrategy
  ): F[Option[Element[F]]] =
    Sync[F].delay(
      Option(element.findElement(elementLocationStrategy.asSelenium)).map(new UmbreonElement[F](_))
    )

  def findElements(
    elementLocationStrategy: ElementLocationStrategy
  ): F[List[Element[F]]] =
    Sync[F].delay(
      element
        .findElements(elementLocationStrategy.asSelenium)
        .asScala
        .toList
        .map(new UmbreonElement[F](_))
    )

  def screenshot: F[Array[Byte]] = Sync[F].delay(element.getScreenshotAs(SeleniumOutputType.BYTES))

  def selected: F[Boolean] = Sync[F].delay(element.isSelected)

  def attribute(name: String): F[String] = Sync[F].delay(element.getAttribute(name))

  def hasAttribute(name: String): F[Boolean] = Sync[F].map(attribute(name))(name => Option(name).isDefined)

  def property(name: String): F[String] = attribute(name)

  def css(name: String): F[String] = Sync[F].delay(element.getCssValue(name))

  def text(whitespace: String = "WS"): F[String] = Sync[F].delay(element.getText)

  def name: F[String] = Sync[F].delay(element.getTagName)

  def rect: F[Rect] = Sync[F].delay(element.getRect.asLunium)

  def enabled: F[Boolean] = Sync[F].delay(element.isEnabled)

  def click: F[Unit] = Sync[F].delay(element.click())

  def clear: F[Unit] = Sync[F].delay(element.clear())

  def sendKeys(keys: Keys): F[Unit] = Sync[F].delay(element.sendKeys(keys.asSelenium))

}

object UmbreonElement {
  def apply[F[_]: Sync](element: SeleniumWebElement): Element[F] = new UmbreonElement[F](element)
}
