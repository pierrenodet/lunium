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

package lunium.zelenium

import org.openqa.selenium.{
  NoSuchCookieException => SeleniumNoSuchCookieException,
  InvalidCookieDomainException => SeleniumInvalidCookieDomainException,
  InvalidSelectorException => SeleniumInvalidSelectorException,
  NoSuchElementException => SeleniumNoSuchElementException,
  OutputType => SeleniumOutputType,
  Rectangle => SeleniumRectangle,
  TimeoutException => SeleniumTimeoutException,
  UnsupportedCommandException => SeleniumUnsupportedCommandException
}
import org.openqa.selenium.remote.{ RemoteWebDriver => SeleniumRemoteWebDriver }
import java.util.concurrent.TimeUnit

import scala.jdk.CollectionConverters._
import java.net.URL

import lunium._
import zio._
import lunium.selenium.implicits._

import scala.util.Try

class ZeleniumSession(private[lunium] val rwd: SeleniumRemoteWebDriver) extends Session[IO] {

  def findElement(
    elementLocationStrategy: ElementLocationStrategy
  ): IO[SearchElementException, ZeleniumElement] =
    IO.fromEither(
      try {
        Right(ZeleniumElement(rwd.findElement(elementLocationStrategy.asSelenium)))
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
          rwd
            .findElements(elementLocationStrategy.asSelenium)
            .asScala
            .toList
            .map(new ZeleniumElement(_))
        )
      } catch {
        case e: SeleniumInvalidSelectorException => Left(new InvalidSelectorException(e.getMessage()))
        case e: SeleniumNoSuchElementException   => Left(new NoSuchElementException(e.getMessage()))
      }
    )

  def screenshot: IO[Nothing, Array[Byte]] = UIO(rwd.getScreenshotAs(SeleniumOutputType.BYTES))

  def timeout: IO[Nothing, Timeout] = UIO.succeed(new Timeout(1))

  def setTimeout(timeout: Timeout): IO[Nothing, Unit] =
    for {
      _ <- UIO(
            rwd.manage.timeouts.implicitlyWait(timeout.implicitWait, TimeUnit.MILLISECONDS)
          )
      _ <- UIO(
            rwd.manage.timeouts.pageLoadTimeout(timeout.pageLoad, TimeUnit.MILLISECONDS)
          )
      _ <- UIO(
            rwd.manage.timeouts
              .setScriptTimeout(timeout.script.getOrElse(-1), TimeUnit.MILLISECONDS)
          )
    } yield ()

  def navigate(command: NavigationCommand): IO[NavigationException, Unit] = IO.fromEither {
    try {
      command match {
        case Forward    => Right(rwd.navigate().forward())
        case Url(value) => Right(rwd.navigate().to(value))
        case Back       => Right(rwd.navigate().back())
        case Refresh    => Right(rwd.navigate().refresh())
      }
    } catch {
      case e: SeleniumTimeoutException => Left(new TimeoutException(e.getMessage()))
    }
  }

  def url: IO[Nothing, String]   = UIO.apply(rwd.getCurrentUrl)
  def title: IO[Nothing, String] = UIO.apply(rwd.getTitle)

  def contexts: IO[Nothing, List[ContextType]] =
    UIO(rwd.getWindowHandles.asScala.toList.flatMap(wh => ContextType.fromString(wh).toOption))

  def current: IO[Nothing, ContextType] = UIO(ContextType.fromString(rwd.getWindowHandle).getOrElse(Default))

  def deleteCookies(): IO[Nothing, Unit] = UIO(rwd.manage().deleteAllCookies())

  def addCookie(cookie: Cookie): IO[InvalidCookieDomainException, Unit] =
    IO.fromEither(try {
      Right(rwd.manage().addCookie(cookie.asSelenium))
    } catch {
      case e: SeleniumInvalidCookieDomainException => Left(new InvalidCookieDomainException(e.getMessage()))
    })

  def cookies: IO[Nothing, List[Cookie]] = UIO(rwd.manage().getCookies.asScala.map(_.asLunium).toList)

  def deleteCookie(name: String): IO[Nothing, Unit] = UIO(rwd.manage().deleteAllCookies())

  def findCookie(name: String): IO[NoSuchCookieException, Cookie] =
    IO.fromEither(try {
      Right(rwd.manage().getCookieNamed(name).asLunium)
    } catch {
      case e: SeleniumNoSuchCookieException => Left(new NoSuchCookieException(e.getMessage()))
      case e: java.lang.NullPointerException          => Left(new NoSuchCookieException(e.getMessage()))
    })

  def executeSync(script: Script): IO[Throwable, String] = IO.effect(rwd.executeScript(script.value).toString)

  def executeAsync(script: Script): IO[Throwable, String] = IO.effect(rwd.executeAsyncScript(script.value).toString)

  def source: IO[Throwable, PageSource] = IO.effect(PageSource(rwd.getPageSource))

  def resize(rect: Rect): IO[UnsupportedOperationException, Unit] = IO.fromEither {
    try {
      rwd.manage().window().setSize(rect.asSeleniumDimension)
      rwd.manage().window().setPosition(rect.asSeleniumPoint)
      Right(())
    } catch {
      case e: SeleniumUnsupportedCommandException => Left(new UnsupportedOperationException(e.getMessage()))
    }
  }

  def setState(windowState: WindowState): IO[UnsupportedOperationException, Unit] =
    IO.fromEither(
      try {
        windowState match {
          case FullScreen => rwd.manage().window().fullscreen()
          case Maximized  => rwd.manage().window().maximize()
          case Minimized  => rwd.manage().window().setPosition(new Rect(0, -1000, 0, 0).asSeleniumPoint)
        }
        Right(())
      } catch {
        case e: SeleniumUnsupportedCommandException => Left(new UnsupportedOperationException(e.getMessage()))
      }
    )

  def rect: IO[Nothing, Rect] =
    UIO.apply(new SeleniumRectangle(rwd.manage().window().getPosition, rwd.manage().window().getSize).asLunium)

}

object ZeleniumSession {
  def apply(
    host: String,
    port: String,
    capabilities: Capabilities
  ): Managed[Throwable, ZeleniumSession] =
    Managed.make(
      IO.effect(
        new ZeleniumSession(new SeleniumRemoteWebDriver(new URL(s"http://$host:$port"), capabilities.asSelenium))
      )
    )(css => UIO.apply(css.rwd.quit()))

  def newTab(
    zs: ZeleniumSession
  ): Managed[Throwable, ZeleniumSession] =
    Managed.make(zs.executeSync(Script("window.open()")).map(_ => zs))(
      css => UIO.apply(css.rwd.close())
    )

  def fromContextType(
    zs: ZeleniumSession,
    contextType: ContextType
  ): Managed[Throwable, ZeleniumSession] = {

    val fcct = zs.current

    def switchTo(contextType: ContextType, zs: ZeleniumSession): Task[SeleniumRemoteWebDriver] =
      IO.effect(contextType match {
        case Default           => zs.rwd.switchTo.defaultContent().asInstanceOf[SeleniumRemoteWebDriver]
        case Frame(id)         => zs.rwd.switchTo.frame(id).asInstanceOf[SeleniumRemoteWebDriver]
        case Parent            => zs.rwd.switchTo.parentFrame().asInstanceOf[SeleniumRemoteWebDriver]
        case Window(handle, _) => zs.rwd.switchTo.window(handle).asInstanceOf[SeleniumRemoteWebDriver]
      })

    Managed.make(switchTo(contextType, zs).map(_ => zs))(zs => {
      zs.rwd.close()
      fcct.succeedLazy(cct => switchTo(cct, zs))
    })
  }

}
