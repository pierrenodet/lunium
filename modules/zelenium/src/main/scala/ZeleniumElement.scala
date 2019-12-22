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
import org.openqa.selenium.{
  InvalidElementStateException => SeleniumInvalidElementStateException,
  StaleElementReferenceException => SeleniumStaleElementReferenceException,
  WebElement => SeleniumWebElement,
  NoSuchCookieException => SeleniumNoSuchCookieException,
  InvalidCookieDomainException => SeleniumInvalidCookieDomainException,
  InvalidSelectorException => SeleniumInvalidSelectorException,
  NoSuchElementException => SeleniumNoSuchElementException,
  OutputType => SeleniumOutputType,
  Rectangle => SeleniumRectangle,
  TimeoutException => SeleniumTimeoutException,
  UnsupportedCommandException => SeleniumUnsupportedCommandException
}
import zio._
import scala.jdk.CollectionConverters._

class ZeleniumElement(private[lunium] val wb: SeleniumWebElement) extends Element[IO] {

  def findElement(
    elementLocationStrategy: ElementLocationStrategy
  ): IO[SearchElementException, ZeleniumElement] =
    IO.fromEither(
      try {
        Right(ZeleniumElement(wb.findElement(elementLocationStrategy.asSelenium)))
      } catch {
        case e: SeleniumInvalidSelectorException => Left(new InvalidSelectorException(e.getMessage()))
        case e: SeleniumNoSuchElementException   => Left(new NoSuchElementException(e.getMessage()))
      }
    )

  def findElements(
    elementLocationStrategy: ElementLocationStrategy
  ): IO[SearchElementException, List[ZeleniumElement]] =
    IO.fromEither(
      try {
        Right(
          wb.findElements(elementLocationStrategy.asSelenium)
            .asScala
            .toList
            .map(new ZeleniumElement(_))
        )
      } catch {
        case e: SeleniumInvalidSelectorException => Left(new InvalidSelectorException(e.getMessage()))
        case e: SeleniumNoSuchElementException   => Left(new NoSuchElementException(e.getMessage()))
      }
    )

  def screenshot: IO[Nothing, Array[Byte]] = UIO(wb.getScreenshotAs(SeleniumOutputType.BYTES))

  def isSelected: IO[StaleElementReferenceException, Boolean] =
    IO.fromEither(try {
      Right(wb.isSelected)
    } catch {
      case e: SeleniumStaleElementReferenceException => Left(new StaleElementReferenceException(e.getMessage()))
    })

  def attribute(name: String): IO[StaleElementReferenceException, String] =
    IO.fromEither(try {
      Right(wb.getAttribute(name))
    } catch {
      case e: SeleniumStaleElementReferenceException => Left(new StaleElementReferenceException(e.getMessage()))
    })

  def hasAttribute(name: String): IO[StaleElementReferenceException, Boolean] =
    IO.fromEither(try {
      Right(attribute(name)).map(name => Option(name).isDefined)
    } catch {
      case e: SeleniumStaleElementReferenceException => Left(new StaleElementReferenceException(e.getMessage()))
    })

  def property(name: String): IO[StaleElementReferenceException, String] = attribute(name)

  def css(name: String): IO[StaleElementReferenceException, String] =
    IO.fromEither(try {
      Right(wb.getCssValue(name))
    } catch {
      case e: SeleniumStaleElementReferenceException => Left(new StaleElementReferenceException(e.getMessage()))
    })

  def text(whitespace: String = "WS"): IO[StaleElementReferenceException, String] =
    IO.fromEither(try {
      Right(wb.getText)
    } catch {
      case e: SeleniumStaleElementReferenceException => Left(new StaleElementReferenceException(e.getMessage()))
    })

  def name: IO[StaleElementReferenceException, String] =
    IO.fromEither(try {
      Right(wb.getTagName)
    } catch {
      case e: SeleniumStaleElementReferenceException => Left(new StaleElementReferenceException(e.getMessage()))
    })

  def rect: IO[StaleElementReferenceException, Rect] =
    IO.fromEither(try {
      Right(wb.getRect.asLunium)
    } catch {
      case e: SeleniumStaleElementReferenceException => Left(new StaleElementReferenceException(e.getMessage()))
    })

  def isEnabled: IO[StaleElementReferenceException, Boolean] =
    IO.fromEither(try {
      Right(wb.isEnabled)
    } catch {
      case e: SeleniumStaleElementReferenceException => Left(new StaleElementReferenceException(e.getMessage()))
    })

  def click: IO[InteractElementException, Unit] =
    IO.fromEither(try {
      Right(wb.click())
    } catch {
      case e: SeleniumInvalidSelectorException     => Left(new ElementClickInterceptedException(e.getMessage()))
      case e: SeleniumNoSuchElementException       => Left(new ElementNotInteractableException(e.getMessage()))
      case e: SeleniumInvalidElementStateException => Left(new InvalidElementStateException(e.getMessage()))
    })

  def clear: IO[InteractElementException, Unit] =
    IO.fromEither(try {
      Right(wb.clear())
    } catch {
      case e: SeleniumInvalidSelectorException     => Left(new ElementClickInterceptedException(e.getMessage()))
      case e: SeleniumNoSuchElementException       => Left(new ElementNotInteractableException(e.getMessage()))
      case e: SeleniumInvalidElementStateException => Left(new InvalidElementStateException(e.getMessage()))
    })

  def sendKeys(keys: Keys): IO[InteractElementException, Unit] =
    IO.fromEither(try {
      Right(wb.sendKeys(keys.asSelenium))
    } catch {
      case e: SeleniumInvalidSelectorException     => Left(new ElementClickInterceptedException(e.getMessage()))
      case e: SeleniumNoSuchElementException       => Left(new ElementNotInteractableException(e.getMessage()))
      case e: SeleniumInvalidElementStateException => Left(new InvalidElementStateException(e.getMessage()))
    })

}

object ZeleniumElement {
  def apply(element: SeleniumWebElement): ZeleniumElement = new ZeleniumElement(element)
}
