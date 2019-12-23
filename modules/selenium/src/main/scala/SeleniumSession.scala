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

import java.util.concurrent.TimeUnit

import lunium._
import lunium.selenium.implicits._
import org.openqa.selenium.remote.{ RemoteWebDriver => SeleniumRemoteWebDriver }
import org.openqa.selenium.{
  InvalidCookieDomainException => SeleniumInvalidCookieDomainException,
  InvalidSelectorException => SeleniumInvalidSelectorException,
  NoAlertPresentException => SeleniumNoAlertPresentException,
  NoSuchCookieException => SeleniumNoSuchCookieException,
  NoSuchElementException => SeleniumNoSuchElementException,
  OutputType => SeleniumOutputType,
  Rectangle => SeleniumRectangle,
  TimeoutException => SeleniumTimeoutException,
  UnsupportedCommandException => SeleniumUnsupportedCommandException
}

import scala.collection.JavaConverters._
import scala.util.Try

class SeleniumSession(private[lunium] val rwd: SeleniumRemoteWebDriver) extends Session[Either[*, *]] {

  def findElement(
    elementLocationStrategy: ElementLocationStrategy
  ): Either[SearchElementException, SeleniumElement] =
    try {
      Right(new SeleniumElement(rwd.findElement(elementLocationStrategy.asSelenium)))
    } catch {
      case e: SeleniumInvalidSelectorException => Left(new InvalidSelectorException(e.getMessage))
      case e: SeleniumNoSuchElementException   => Left(new NoSuchElementException(e.getMessage))
    }

  def findElements(
    elementLocationStrategy: ElementLocationStrategy
  ): Either[SearchElementException, List[SeleniumElement]] =
    try {
      Right(
        rwd
          .findElements(elementLocationStrategy.asSelenium)
          .asScala
          .toList
          .map(new SeleniumElement(_))
      )
    } catch {
      case e: SeleniumInvalidSelectorException => Left(new InvalidSelectorException(e.getMessage))
      case e: SeleniumNoSuchElementException   => Left(new NoSuchElementException(e.getMessage))
    }

  def screenshot: Either[Nothing, Array[Byte]] = Right(rwd.getScreenshotAs(SeleniumOutputType.BYTES))

  def timeouts: Either[Nothing, Timeouts] = Right(new Timeouts(1))

  def setTimeouts(timeouts: Timeouts): Either[Nothing, Unit] =
    for {
      _ <- Right(
            rwd.manage.timeouts.implicitlyWait(timeouts.implicitWait, TimeUnit.MILLISECONDS)
          )
      _ <- Right(
            rwd.manage.timeouts.pageLoadTimeout(timeouts.pageLoad, TimeUnit.MILLISECONDS)
          )
      _ <- Right(
            rwd.manage.timeouts
              .setScriptTimeout(timeouts.script.getOrElse(-1), TimeUnit.MILLISECONDS)
          )
    } yield ()

  def navigate(navigationCommand: NavigationCommand): Either[NavigationException, Unit] =
    try {
      navigationCommand match {
        case Forward    => Right(rwd.navigate().forward())
        case Url(value) => Right(rwd.navigate().to(value))
        case Back       => Right(rwd.navigate().back())
        case Refresh    => Right(rwd.navigate().refresh())
      }
    } catch {
      case e: SeleniumTimeoutException => Left(new TimeoutException(e.getMessage))
    }

  def url: Either[Nothing, Url]      = Right(new Url(rwd.getCurrentUrl))
  def title: Either[Nothing, String] = Right(rwd.getTitle)

  def contexts: Either[Nothing, List[ContextType]] =
    Right(rwd.getWindowHandles.asScala.toList.flatMap(wh => ContextType.fromString(wh).toOption))

  def current: Either[Nothing, ContextType] =
    Right(ContextType.fromString(rwd.getWindowHandle.tail.tail).getOrElse(Default))

  def deleteCookies(): Either[Nothing, Unit] = Right(rwd.manage().deleteAllCookies())

  def addCookie(cookie: Cookie): Either[InvalidCookieDomainException, Unit] =
    try {
      Right(rwd.manage().addCookie(cookie.asSelenium))
    } catch {
      case e: SeleniumInvalidCookieDomainException => Left(new InvalidCookieDomainException(e.getMessage))
    }

  def cookies: Either[Nothing, List[Cookie]] = Right(rwd.manage().getCookies.asScala.map(_.asLunium).toList)

  def deleteCookie(name: String): Either[Nothing, Unit] = Right(rwd.manage().deleteAllCookies())

  def findCookie(name: String): Either[NoSuchCookieException, Cookie] =
    try {
      Right(rwd.manage().getCookieNamed(name).asLunium)
    } catch {
      case e: SeleniumNoSuchCookieException  => Left(new NoSuchCookieException(e.getMessage))
      case e: java.lang.NullPointerException => Left(new NoSuchCookieException(e.getMessage))
    }

  def executeScript(script: Script, executionMode: ExecutionMode): Either[Throwable, String] = executionMode match {
    case SyncExecution  => Try(rwd.executeScript(script.value).toString).toEither
    case AsyncExecution => Try(rwd.executeAsyncScript(script.value).toString).toEither
  }

  def source: Either[Throwable, PageSource] = Try(PageSource(rwd.getPageSource)).toEither

  def setState(windowState: WindowState): Either[UnsupportedOperationException, Unit] =
    try {
      windowState match {
        case FullScreen => rwd.manage().window().fullscreen()
        case Maximized  => rwd.manage().window().maximize()
        case Minimized  => rwd.manage().window().setPosition(new Rectangle(0, -1000, 0, 0).asSeleniumPoint)
        case rect: Rectangle =>
          rwd.manage().window().setSize(rect.asSeleniumDimension)
          rwd.manage().window().setPosition(rect.asSeleniumPoint)
      }
      Right(())
    } catch {
      case e: SeleniumUnsupportedCommandException => Left(new UnsupportedOperationException(e.getMessage))
    }

  def rectangle: Either[Nothing, Rectangle] =
    Right(new SeleniumRectangle(rwd.manage().window().getPosition, rwd.manage().window().getSize).asLunium)

  def dismiss: Either[NoSuchAlertException, Unit] =
    try {
      Right(rwd.switchTo().alert().dismiss())
    } catch {
      case e: SeleniumNoAlertPresentException => Left(new NoSuchAlertException(e.getMessage))
    }

  def accept: Either[NoSuchAlertException, Unit] =
    try {
      Right(rwd.switchTo().alert().accept())
    } catch {
      case e: SeleniumNoAlertPresentException => Left(new NoSuchAlertException(e.getMessage))
    }

  def alertText: Either[NoSuchAlertException, Option[String]] =
    try {
      Right(Option(rwd.switchTo().alert().getText))
    } catch {
      case e: SeleniumNoAlertPresentException => Left(new NoSuchAlertException(e.getMessage))
    }

  def setAlertText(text: String): Either[SendAlertTextException, Unit] =
    try {
      Right(rwd.switchTo().alert().sendKeys(text))
    } catch {
      case e: SeleniumNoAlertPresentException => Left(new NoSuchAlertException(e.getMessage))
    }

}
