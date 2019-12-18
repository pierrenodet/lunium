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
import cats.data.EitherT

class UmbreonElement[F[_]: Sync](private[lunium] val element: SeleniumWebElement) extends Element[EitherT[F, ?, ?]] {

  def findElement(
    elementLocationStrategy: ElementLocationStrategy
  ): EitherT[F, Throwable, Option[Element[EitherT[F, ?, ?]]]] =
    EitherT.liftF(
      Sync[F].delay(
        Option(element.findElement(elementLocationStrategy.asSelenium)).map(new UmbreonElement[F](_))
      )
    )

  def findElements(
    elementLocationStrategy: ElementLocationStrategy
  ): EitherT[F, Throwable, List[Element[EitherT[F, ?, ?]]]] =
    EitherT.liftF(
      Sync[F].delay(
        element
          .findElements(elementLocationStrategy.asSelenium)
          .asScala
          .toList
          .map(new UmbreonElement[F](_))
      )
    )

  def screenshot: EitherT[F, Throwable, Array[Byte]] =
    EitherT.liftF(Sync[F].delay(element.getScreenshotAs(SeleniumOutputType.BYTES)))

  def selected: EitherT[F, Throwable, Boolean] = EitherT.liftF(Sync[F].delay(element.isSelected))

  def attribute(name: String): EitherT[F, Throwable, String] = EitherT.liftF(Sync[F].delay(element.getAttribute(name)))

  def hasAttribute(name: String): EitherT[F, Throwable, Boolean] = attribute(name).map(name => Option(name).isDefined)

  def property(name: String): EitherT[F, Throwable, String] = attribute(name)

  def css(name: String): EitherT[F, Throwable, String] = EitherT.liftF(Sync[F].delay(element.getCssValue(name)))

  def text(whitespace: String = "WS"): EitherT[F, Throwable, String] = EitherT.liftF(Sync[F].delay(element.getText))

  def name: EitherT[F, Throwable, String] = EitherT.liftF(Sync[F].delay(element.getTagName))

  def rect: EitherT[F, Throwable, Rect] = EitherT.liftF(Sync[F].delay(element.getRect.asLunium))

  def enabled: EitherT[F, Throwable, Boolean] = EitherT.liftF(Sync[F].delay(element.isEnabled))

  def click: EitherT[F, Throwable, Unit] = EitherT.liftF(Sync[F].delay(element.click()))

  def clear: EitherT[F, Throwable, Unit] = EitherT.liftF(Sync[F].delay(element.clear()))

  def sendKeys(keys: Keys): EitherT[F, Throwable, Unit] =
    EitherT.liftF(Sync[F].delay(element.sendKeys(keys.asSelenium)))

}

object UmbreonElement {
  def apply[F[_]: Sync](element: SeleniumWebElement): Element[EitherT[F, ?, ?]] = new UmbreonElement[F](element)
}
