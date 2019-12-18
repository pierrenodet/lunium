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

class ZeleniumElement(element: SeleniumWebElement) extends Element[Task] {

  def findElement(
    elementLocationStrategy: ElementLocationStrategy
  ): Task[Option[Element[Task]]] =
    Task.effect(Option(element.findElement(elementLocationStrategy.asSelenium)).map(new ZeleniumElement(_)))

  def findElements(
    elementLocationStrategy: ElementLocationStrategy
  ): Task[List[Element[Task]]] =
    Task.effect(
      element
        .findElements(elementLocationStrategy.asSelenium)
        .asScala
        .toList
        .map(new ZeleniumElement(_))
    )

  def screenshot: Task[Array[Byte]] = Task.effect(element.getScreenshotAs(SeleniumOutputType.BYTES))

  def selected: Task[Boolean] = Task.effect(element.isSelected)

  def attribute(name: String): Task[String] = Task.effect(element.getAttribute(name))

  def hasAttribute(name: String): Task[Boolean] =
    Task.effectSuspendTotal(attribute(name)).map(name => Option(name).isDefined)

  def property(name: String): Task[String] = attribute(name)

  def css(name: String): Task[String] = Task.effect(element.getCssValue(name))

  def text(whitespace: String = "WS"): Task[String] = Task.effect(element.getText)

  def name: Task[String] = Task.effect(element.getTagName)

  def rect: Task[Rect] = Task.effect(element.getRect.asLunium)

  def enabled: Task[Boolean] = Task.effect(element.isEnabled)

  def click: Task[Unit] = Task.effect(element.click())

  def clear: Task[Unit] = Task.effect(element.clear())

  def sendKeys(keys: Keys): Task[Unit] = Task.effect(element.sendKeys(keys.asSelenium))

}

object ZeleniumElement {
  def apply(element: SeleniumWebElement): Element[Task] = new ZeleniumElement(element)
}
