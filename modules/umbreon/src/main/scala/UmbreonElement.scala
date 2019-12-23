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

class UmbreonElement[F[_]: Sync](private[lunium] val wb: SeleniumWebElement) extends Element[EitherT[F, *, *]] {

  def findElement(
    elementLocationStrategy: ElementLocationStrategy
  ): EitherT[F, SearchElementException, Element[EitherT[F, *, *]]] =
    EitherT.fromEither(
      try {
        Right(UmbreonElement(wb.findElement(elementLocationStrategy.asSelenium)))
      } catch {
        case e: SeleniumInvalidSelectorException => Left(new InvalidSelectorException(e.getMessage))
        case e: SeleniumNoSuchElementException   => Left(new NoSuchElementException(e.getMessage))
      }
    )

  def findElements(
    elementLocationStrategy: ElementLocationStrategy
  ): EitherT[F, SearchElementException, List[Element[EitherT[F, *, *]]]] =
    EitherT.fromEither(
      try {
        Right(
          wb.findElements(elementLocationStrategy.asSelenium)
            .asScala
            .toList
            .map(UmbreonElement(_))
        )
      } catch {
        case e: SeleniumInvalidSelectorException => Left(new InvalidSelectorException(e.getMessage))
        case e: SeleniumNoSuchElementException   => Left(new NoSuchElementException(e.getMessage))
      }
    )

  def screenshot: EitherT[F, Nothing, Array[Byte]] = EitherT.pure(wb.getScreenshotAs(SeleniumOutputType.BYTES))

  def selected: EitherT[F, StaleElementReferenceException, Boolean] =
    EitherT.fromEither(try {
      Right(wb.isSelected)
    } catch {
      case e: SeleniumStaleElementReferenceException => Left(new StaleElementReferenceException(e.getMessage))
    })

  def attribute(name: String): EitherT[F, StaleElementReferenceException, Option[String]] =
    EitherT.fromEither(try {
      Right(Option(wb.getAttribute(name)))
    } catch {
      case e: SeleniumStaleElementReferenceException => Left(new StaleElementReferenceException(e.getMessage))
    })

  def hasAttribute(name: String): EitherT[F, StaleElementReferenceException, Boolean] = attribute(name).map(_.isDefined)

  def property(name: String): EitherT[F, StaleElementReferenceException, Option[String]] = attribute(name)

  def css(name: String): EitherT[F, StaleElementReferenceException, Option[String]] =
    EitherT.fromEither(try {
      Right(Option(wb.getAttribute(name)))
    } catch {
      case e: SeleniumStaleElementReferenceException => Left(new StaleElementReferenceException(e.getMessage))
    })

  def text(whitespace: String = "WS"): EitherT[F, StaleElementReferenceException, String] =
    EitherT.fromEither(try {
      Right(wb.getText)
    } catch {
      case e: SeleniumStaleElementReferenceException => Left(new StaleElementReferenceException(e.getMessage))
    })

  def name: EitherT[F, StaleElementReferenceException, String] =
    EitherT.fromEither(try {
      Right(wb.getTagName)
    } catch {
      case e: SeleniumStaleElementReferenceException => Left(new StaleElementReferenceException(e.getMessage))
    })

  def rectangle: EitherT[F, StaleElementReferenceException, Rectangle] =
    EitherT.fromEither(try {
      Right(wb.getRect.asLunium)
    } catch {
      case e: SeleniumStaleElementReferenceException => Left(new StaleElementReferenceException(e.getMessage))
    })

  def enabled: EitherT[F, StaleElementReferenceException, Boolean] =
    EitherT.fromEither(try {
      Right(wb.isEnabled)
    } catch {
      case e: SeleniumStaleElementReferenceException => Left(new StaleElementReferenceException(e.getMessage))
    })

  def click: EitherT[F, InteractElementException, Unit] =
    EitherT.fromEither(try {
      Right(wb.click())
    } catch {
      case e: SeleniumElementClickInterceptedException => Left(new ElementClickInterceptedException(e.getMessage))
      case e: SeleniumElementNotInteractableException  => Left(new ElementNotInteractableException(e.getMessage))
      case e: SeleniumInvalidElementStateException     => Left(new InvalidElementStateException(e.getMessage))
    })

  def clear: EitherT[F, InteractElementException, Unit] =
    EitherT.fromEither(try {
      Right(wb.clear())
    } catch {
      case e: SeleniumElementClickInterceptedException => Left(new ElementClickInterceptedException(e.getMessage))
      case e: SeleniumElementNotInteractableException  => Left(new ElementNotInteractableException(e.getMessage))
      case e: SeleniumInvalidElementStateException     => Left(new InvalidElementStateException(e.getMessage))
    })

  def sendKeys(keys: Keys): EitherT[F, InteractElementException, Unit] =
    EitherT.fromEither(try {
      Right(wb.sendKeys(keys.asSelenium))
    } catch {
      case e: SeleniumElementClickInterceptedException => Left(new ElementClickInterceptedException(e.getMessage))
      case e: SeleniumElementNotInteractableException  => Left(new ElementNotInteractableException(e.getMessage))
      case e: SeleniumInvalidElementStateException     => Left(new InvalidElementStateException(e.getMessage))
    })
}

object UmbreonElement {
  def apply[F[_]: Sync](element: SeleniumWebElement): Element[EitherT[F, *, *]] = new UmbreonElement[F](element)
}
