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

import java.net.URL

import cats.data.EitherT
import cats.effect._
import lunium._
import lunium.selenium._
import lunium.selenium.implicits._
import org.openqa.selenium.chrome.{ ChromeDriver => SeleniumChromeDriver, ChromeOptions => SeleniumChromeOptions }
import org.openqa.selenium.remote.{ RemoteWebDriver => SeleniumRemoteWebDriver }

class UmbreonSession[F[_]: Sync](private[lunium] val ss: SeleniumSession) extends Session[EitherT[F, *, *]] {

  def findElement(
    elementLocationStrategy: ElementLocationStrategy
  ): EitherT[F, SearchElementException, Element[EitherT[F, *, *]]] =
    EitherT.fromEither(ss.findElement(elementLocationStrategy).map(se => new UmbreonElement[F](se)))

  def findElements(
    elementLocationStrategy: ElementLocationStrategy
  ): EitherT[F, SearchElementException, List[Element[EitherT[F, *, *]]]] =
    EitherT.fromEither(ss.findElements(elementLocationStrategy).map(_.map(se => new UmbreonElement[F](se))))

  def screenshot: EitherT[F, Nothing, Array[Byte]] = EitherT.fromEither(ss.screenshot)

  def timeouts: EitherT[F, Nothing, Timeouts] = EitherT.fromEither(ss.timeouts)

  def setTimeouts(timeouts: Timeouts): EitherT[F, Nothing, Unit] =
    EitherT.fromEither(ss.setTimeouts(timeouts))

  def navigate(navigationCommand: NavigationCommand): EitherT[F, NavigationException, Unit] =
    EitherT.fromEither(ss.navigate(navigationCommand))

  def url: EitherT[F, Nothing, Url]      = EitherT.fromEither(ss.url)
  def title: EitherT[F, Nothing, String] = EitherT.fromEither(ss.title)

  def contexts: EitherT[F, Nothing, List[ContextType]] =
    EitherT.fromEither(ss.contexts)

  def current: EitherT[F, Nothing, ContextType] =
    EitherT.fromEither(ss.current)

  def deleteCookies(): EitherT[F, Nothing, Unit] = EitherT.fromEither(ss.deleteCookies())

  def addCookie(cookie: Cookie): EitherT[F, InvalidCookieDomainException, Unit] =
    EitherT.fromEither(ss.addCookie(cookie))

  def cookies: EitherT[F, Nothing, List[Cookie]] = EitherT.fromEither(ss.cookies)

  def deleteCookie(name: String): EitherT[F, Nothing, Unit] = EitherT.fromEither(ss.deleteCookie(name))

  def findCookie(name: String): EitherT[F, NoSuchCookieException, Cookie] =
    EitherT.fromEither(ss.findCookie(name))

  def executeScript(script: Script, executionMode: ExecutionMode): EitherT[F, Throwable, String] =
    EitherT.fromEither(ss.executeScript(script, executionMode))

  def source: EitherT[F, Throwable, PageSource] = EitherT.fromEither(ss.source)

  def setState(windowState: WindowState): EitherT[F, UnsupportedOperationException, Unit] =
    EitherT.fromEither(ss.setState(windowState))

  def rectangle: EitherT[F, Nothing, Rectangle] = EitherT.fromEither(ss.rectangle)

  def dismiss: EitherT[F, NoSuchAlertException, Unit] =
    EitherT.fromEither(ss.dismiss)

  def accept: EitherT[F, NoSuchAlertException, Unit] =
    EitherT.fromEither(ss.accept)

  def alertText: EitherT[F, NoSuchAlertException, Option[String]] =
    EitherT.fromEither(ss.alertText)

  def setAlertText(text: String): EitherT[F, SendAlertTextException, Unit] =
    EitherT.fromEither(ss.setAlertText(text))

}

object UmbreonSession {

  def fromCapabilities[F[_]: Sync](
    host: String,
    port: String,
    capabilities: Capabilities
  ): Resource[F, UmbreonSession[F]] =
    Resource.make(
      Sync[F].delay(
        new UmbreonSession[F](
          new SeleniumSession(new SeleniumRemoteWebDriver(new URL(s"http://$host:$port"), capabilities.asSelenium))
        )
      )
    )(
      css => Sync[F].delay(css.ss.rwd.quit())
    )

  def headlessChrome[F[_]: Sync]: Resource[F, UmbreonSession[F]] =
    Resource.make(
      Sync[F].delay(
        new UmbreonSession[F](
          new SeleniumSession(new SeleniumChromeDriver(new SeleniumChromeOptions().addArguments("--headless")))
        )
      )
    )(
      css => Sync[F].delay(css.ss.rwd.quit())
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
