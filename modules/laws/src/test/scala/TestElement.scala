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

import cats.effect._
import lunium._
import lunium.selenium.implicits._
import org.openqa.selenium.{ OutputType => SeleniumOutputType, WebElement => SeleniumWebElement }
import scala.jdk.CollectionConverters._
import cats.Id
import cats.data.EitherT
/*
class TestElement(private[lunium] val element: SeleniumWebElement) extends Element[EitherT[Id,?,?]] {

  def findElement(
    elementLocationStrategy: ElementLocationStrategy
  ): Id[Option[Element[Id]]] =

      Option(element.findElement(elementLocationStrategy.asSelenium)).map(new TestElement(_))


  def findElements(
    elementLocationStrategy: ElementLocationStrategy
  ): Id[List[Element[Id]]] =

      element
        .findElements(elementLocationStrategy.asSelenium)
        .asScala
        .toList
        .map(new TestElement(_))


  def screenshot: Id[Array[Byte]] = element.getScreenshotAs(SeleniumOutputType.BYTES)

  def selected: Id[Boolean] = element.isSelected

  def attribute(name: String): Id[String] = element.getAttribute(name)

  def hasAttribute(name: String): Id[Boolean] = Option(attribute(name)).isDefined

  def property(name: String): Id[String] = attribute(name)

  def css(name: String): Id[String] = element.getCssValue(name)

  def text(whitespace: String = "WS"): Id[String] = element.getText

  def name: Id[String] = element.getTagName

  def rect: Id[Rect] = element.getRect.asLunium

  def enabled: Id[Boolean] = element.isEnabled

  def click: Id[Unit] = element.click()

  def clear: Id[Unit] = element.clear()

  def sendKeys(keys: Keys): Id[Unit] = element.sendKeys(keys.asSelenium)

}

object TestElement {
  def apply(element: SeleniumWebElement): Element[Id] = new TestElement(element)
}
*/
