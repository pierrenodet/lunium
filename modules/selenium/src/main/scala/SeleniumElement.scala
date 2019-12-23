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

package lunium.selenium

import lunium._
import lunium.selenium.implicits._
import org.openqa.selenium.{
  ElementClickInterceptedException => SeleniumElementClickInterceptedException,
  ElementNotInteractableException => SeleniumElementNotInteractableException,
  InvalidElementStateException => SeleniumInvalidElementStateException,
  InvalidSelectorException => SeleniumInvalidSelectorException,
  NoSuchElementException => SeleniumNoSuchElementException,
  OutputType => SeleniumOutputType,
  StaleElementReferenceException => SeleniumStaleElementReferenceException,
  WebElement => SeleniumWebElement
}

import scala.jdk.CollectionConverters._

class SeleniumElement(private[lunium] val wb: SeleniumWebElement) extends Element[Either[*, *]] {

  def findElement(
    elementLocationStrategy: ElementLocationStrategy
  ): Either[SearchElementException, SeleniumElement] =
    try {
      Right(new SeleniumElement(wb.findElement(elementLocationStrategy.asSelenium)))
    } catch {
      case e: SeleniumInvalidSelectorException => Left(new InvalidSelectorException(e.getMessage))
      case e: SeleniumNoSuchElementException   => Left(new NoSuchElementException(e.getMessage))
    }

  def findElements(
    elementLocationStrategy: ElementLocationStrategy
  ): Either[SearchElementException, List[SeleniumElement]] =
    try {
      Right(
        wb.findElements(elementLocationStrategy.asSelenium)
          .asScala
          .toList
          .map(new SeleniumElement(_))
      )
    } catch {
      case e: SeleniumInvalidSelectorException => Left(new InvalidSelectorException(e.getMessage))
      case e: SeleniumNoSuchElementException   => Left(new NoSuchElementException(e.getMessage))
    }

  def screenshot: Either[Nothing, Array[Byte]] = Right(wb.getScreenshotAs(SeleniumOutputType.BYTES))

  def selected: Either[StaleElementReferenceException, Boolean] =
    try {
      Right(wb.isSelected)
    } catch {
      case e: SeleniumStaleElementReferenceException => Left(new StaleElementReferenceException(e.getMessage))
    }

  def attribute(name: String): Either[StaleElementReferenceException, Option[String]] =
    try {
      Right(Option(wb.getAttribute(name)))
    } catch {
      case e: SeleniumStaleElementReferenceException => Left(new StaleElementReferenceException(e.getMessage))
    }

  def hasAttribute(name: String): Either[StaleElementReferenceException, Boolean] = attribute(name).map(_.isDefined)

  def property(name: String): Either[StaleElementReferenceException, Option[String]] = attribute(name)

  def css(name: String): Either[StaleElementReferenceException, Option[String]] =
    try {
      Right(Option(wb.getAttribute(name)))
    } catch {
      case e: SeleniumStaleElementReferenceException => Left(new StaleElementReferenceException(e.getMessage))
    }

  def text(whitespace: String = "WS"): Either[StaleElementReferenceException, String] =
    try {
      Right(wb.getText)
    } catch {
      case e: SeleniumStaleElementReferenceException => Left(new StaleElementReferenceException(e.getMessage))
    }

  def name: Either[StaleElementReferenceException, String] =
    try {
      Right(wb.getTagName)
    } catch {
      case e: SeleniumStaleElementReferenceException => Left(new StaleElementReferenceException(e.getMessage))
    }

  def rectangle: Either[StaleElementReferenceException, Rectangle] =
    try {
      Right(wb.getRect.asLunium)
    } catch {
      case e: SeleniumStaleElementReferenceException => Left(new StaleElementReferenceException(e.getMessage))
    }

  def enabled: Either[StaleElementReferenceException, Boolean] =
    try {
      Right(wb.isEnabled)
    } catch {
      case e: SeleniumStaleElementReferenceException => Left(new StaleElementReferenceException(e.getMessage))
    }

  def click: Either[InteractElementException, Unit] =
    try {
      Right(wb.click())
    } catch {
      case e: SeleniumElementClickInterceptedException => Left(new ElementClickInterceptedException(e.getMessage))
      case e: SeleniumElementNotInteractableException  => Left(new ElementNotInteractableException(e.getMessage))
      case e: SeleniumInvalidElementStateException     => Left(new InvalidElementStateException(e.getMessage))
    }

  def clear: Either[InteractElementException, Unit] =
    try {
      Right(wb.clear())
    } catch {
      case e: SeleniumElementClickInterceptedException => Left(new ElementClickInterceptedException(e.getMessage))
      case e: SeleniumElementNotInteractableException  => Left(new ElementNotInteractableException(e.getMessage))
      case e: SeleniumInvalidElementStateException     => Left(new InvalidElementStateException(e.getMessage))
    }

  def sendKeys(keys: Keys): Either[InteractElementException, Unit] =
    try {
      Right(wb.sendKeys(keys.asSelenium))
    } catch {
      case e: SeleniumElementClickInterceptedException => Left(new ElementClickInterceptedException(e.getMessage))
      case e: SeleniumElementNotInteractableException  => Left(new ElementNotInteractableException(e.getMessage))
      case e: SeleniumInvalidElementStateException     => Left(new InvalidElementStateException(e.getMessage))
    }
}
