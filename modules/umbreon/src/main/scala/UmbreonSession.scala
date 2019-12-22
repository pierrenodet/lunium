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

import org.openqa.selenium.{ OutputType => SeleniumOutputType, Rectangle => SeleniumRectangle }
import org.openqa.selenium.remote.{ RemoteWebDriver => SeleniumRemoteWebDriver }
import java.util.concurrent.TimeUnit
import java.net.URL
import cats.effect._
import lunium._
import cats.implicits._
import lunium.selenium.implicits._
import scala.jdk.CollectionConverters._
import scala.util.Try
import cats.data.EitherT
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
class UmbreonSession[F[_]: Sync](private[lunium] val rwd: SeleniumRemoteWebDriver) extends Session[EitherT[F, *, *]] {

  def findElement(
    elementLocationStrategy: ElementLocationStrategy
  ): EitherT[F, SearchElementException, Element[EitherT[F, *, *]]] =
    EitherT.fromEither(
      try {
        Right(UmbreonElement(rwd.findElement(elementLocationStrategy.asSelenium)))
      } catch {
        case e: SeleniumInvalidSelectorException => Left(new InvalidSelectorException(e.getMessage()))
        case e: SeleniumNoSuchElementException   => Left(new NoSuchElementException(e.getMessage()))
      }
    )

  def findElements(
    elementLocationStrategy: ElementLocationStrategy
  ): EitherT[F, SearchElementException, List[Element[EitherT[F, *, *]]]] =
    EitherT.fromEither(
      try {
        Right(
          rwd
            .findElements(elementLocationStrategy.asSelenium)
            .asScala
            .toList
            .map(new UmbreonElement(_))
        )
      } catch {
        case e: SeleniumInvalidSelectorException => Left(new InvalidSelectorException(e.getMessage()))
        case e: SeleniumNoSuchElementException   => Left(new NoSuchElementException(e.getMessage()))
      }
    )

  def screenshot: EitherT[F, Nothing, Array[Byte]] = EitherT.pure(rwd.getScreenshotAs(SeleniumOutputType.BYTES))

  def timeout: EitherT[F, Nothing, Timeout] = EitherT.pure(new Timeout(1))

  def setTimeout(timeout: Timeout): EitherT[F, Nothing, Unit] =
    EitherT.liftF(for {
      _ <- Sync[F].delay(
            rwd.manage.timeouts.implicitlyWait(timeout.implicitWait, TimeUnit.MILLISECONDS)
          )
      _ <- Sync[F].delay(
            rwd.manage.timeouts.pageLoadTimeout(timeout.pageLoad, TimeUnit.MILLISECONDS)
          )
      _ <- Sync[F].delay(
            rwd.manage.timeouts
              .setScriptTimeout(timeout.script.getOrElse(-1), TimeUnit.MILLISECONDS)
          )
    } yield ())

  def navigate(command: NavigationCommand): EitherT[F, NavigationException, Unit] = EitherT.fromEither {
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

  def url: EitherT[F, Nothing, String]   = EitherT.pure(rwd.getCurrentUrl)
  def title: EitherT[F, Nothing, String] = EitherT.pure(rwd.getTitle)

  def contexts: EitherT[F, Nothing, List[ContextType]] =
    EitherT.pure(rwd.getWindowHandles.asScala.toList.flatMap(wh => ContextType.fromString(wh).toOption))

  def current: EitherT[F, Nothing, ContextType] =
    EitherT.pure(ContextType.fromString(rwd.getWindowHandle.tail.tail).getOrElse(Default))

  def deleteCookies(): EitherT[F, Nothing, Unit] = EitherT.pure(rwd.manage().deleteAllCookies())

  def addCookie(cookie: Cookie): EitherT[F, InvalidCookieDomainException, Unit] =
    EitherT.fromEither(try {
      Right(rwd.manage().addCookie(cookie.asSelenium))
    } catch {
      case e: SeleniumInvalidCookieDomainException => Left(new InvalidCookieDomainException(e.getMessage()))
    })

  def cookies: EitherT[F, Nothing, List[Cookie]] = EitherT.pure(rwd.manage().getCookies.asScala.map(_.asLunium).toList)

  def deleteCookie(name: String): EitherT[F, Nothing, Unit] = EitherT.pure(rwd.manage().deleteAllCookies())

  def findCookie(name: String): EitherT[F, NoSuchCookieException, Cookie] =
    EitherT.fromEither(try {
      Right(rwd.manage().getCookieNamed(name).asLunium)
    } catch {
      case e: SeleniumNoSuchCookieException  => Left(new NoSuchCookieException(e.getMessage()))
      case e: java.lang.NullPointerException => Left(new NoSuchCookieException(e.getMessage()))
    })

  def executeSync(script: Script): EitherT[F, Throwable, String] =
    EitherT.fromEither(Try(rwd.executeScript(script.value).toString).toEither)

  def executeAsync(script: Script): EitherT[F, Throwable, String] =
    EitherT.fromEither(Try(rwd.executeAsyncScript(script.value).toString).toEither)

  def source: EitherT[F, Throwable, PageSource] = EitherT.fromEither(Try(PageSource(rwd.getPageSource)).toEither)

  def resize(rect: Rect): EitherT[F, UnsupportedOperationException, Unit] = EitherT.fromEither {
    try {
      rwd.manage().window().setSize(rect.asSeleniumDimension)
      rwd.manage().window().setPosition(rect.asSeleniumPoint)
      Right(())
    } catch {
      case e: SeleniumUnsupportedCommandException => Left(new UnsupportedOperationException(e.getMessage()))
    }
  }

  def setState(windowState: WindowState): EitherT[F, UnsupportedOperationException, Unit] =
    EitherT.fromEither(
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

  def rect: EitherT[F, Nothing, Rect] =
    EitherT.pure(new SeleniumRectangle(rwd.manage().window().getPosition, rwd.manage().window().getSize).asLunium)

}

object UmbreonSession {

  def fromCapabilities[F[_]: Sync](
    host: String,
    port: String,
    capabilities: Capabilities
  ): Resource[F, UmbreonSession[F]] =
    Resource.make(
      Sync[F].delay(
        new UmbreonSession[F](new SeleniumRemoteWebDriver(new URL(s"http://$host:$port"), capabilities.asSelenium))
      )
    )(
      css => Sync[F].delay(css.rwd.quit())
    )

  /*
  def newTab[F[_]: Sync](
    css: UmbreonSession[F]
  ): Resource[F, Either[Throwable,UmbreonSession[F]]] =
    Resource.make(css.executeSync(Script("window.open()")).map(_ => css).value)(
      css => Sync[F].pure(css.map(_.rwd.close()))
    )

  def fromContextType[F[_]: Sync](
    css: UmbreonSession[F],
    contextType: ContextType
  ): Resource[F, Either[Throwable,UmbreonSession[F]]] = {

    val fcct = css.current

    def switchTo(contextType: ContextType, css: UmbreonSession[F]):F[Either[Throwable,Unit]] =
      (Sync[F].delay(Right(contextType match {
        case Default           => css.rwd.switchTo.defaultContent()
        case Frame(id)         => css.rwd.switchTo.frame(id)
        case Parent            => css.rwd.switchTo.parentFrame()
        case Window(handle, _) => css.rwd.switchTo.window(handle)
      })))

    Resource.make(switchTo(contextType, css).map(_.map(_ => css)))(ecss => {
      ecss.map(_.rwd.close())
      for {
        _ <- switchTo()
      }
      fcct.value.flatMap(ecct => ecct.flatMap(cct => ecss.map(css => switchTo(cct,css))))
    })
  }
 */

}
