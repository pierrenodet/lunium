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

import java.net.URL

import lunium._
import lunium.selenium._
import lunium.selenium.implicits._
import org.openqa.selenium.remote.{ RemoteWebDriver => SeleniumRemoteWebDriver }
import zio._

class ZeleniumSession(private[lunium] val ss: SeleniumSession) extends Session[IO] {

  def findElement(
    elementLocationStrategy: ElementLocationStrategy
  ): IO[SearchElementException, Element[IO]] =
    IO.fromEither(ss.findElement(elementLocationStrategy).map(se => new ZeleniumElement(se)))

  def findElements(
    elementLocationStrategy: ElementLocationStrategy
  ): IO[SearchElementException, List[Element[IO]]] =
    IO.fromEither(ss.findElements(elementLocationStrategy).map(_.map(se => new ZeleniumElement(se))))

  def screenshot: IO[Nothing, Array[Byte]] = IO.fromEither(ss.screenshot)

  def timeouts: IO[Nothing, Timeouts] = IO.fromEither(ss.timeouts)

  def setTimeouts(timeouts: Timeouts): IO[Nothing, Unit] =
    IO.fromEither(ss.setTimeouts(timeouts))

  def navigate(navigationCommand: NavigationCommand): IO[NavigationException, Unit] =
    IO.fromEither(ss.navigate(navigationCommand))

  def url: IO[Nothing, Url]      = IO.fromEither(ss.url)
  def title: IO[Nothing, String] = IO.fromEither(ss.title)

  def contexts: IO[Nothing, List[ContextType]] =
    IO.fromEither(ss.contexts)

  def current: IO[Nothing, ContextType] =
    IO.fromEither(ss.current)

  def deleteCookies(): IO[Nothing, Unit] = IO.fromEither(ss.deleteCookies())

  def addCookie(cookie: Cookie): IO[InvalidCookieDomainException, Unit] =
    IO.fromEither(ss.addCookie(cookie))

  def cookies: IO[Nothing, List[Cookie]] = IO.fromEither(ss.cookies)

  def deleteCookie(name: String): IO[Nothing, Unit] = IO.fromEither(ss.deleteCookie(name))

  def findCookie(name: String): IO[NoSuchCookieException, Cookie] =
    IO.fromEither(ss.findCookie(name))

  def executeScript(script: Script, executionMode: ExecutionMode): IO[Throwable, String] =
    IO.fromEither(ss.executeScript(script, executionMode))

  def source: IO[Throwable, PageSource] = IO.fromEither(ss.source)

  def setState(windowState: WindowState): IO[UnsupportedOperationException, Unit] =
    IO.fromEither(ss.setState(windowState))

  def rectangle: IO[Nothing, Rectangle] = IO.fromEither(ss.rectangle)

  def dismiss: IO[NoSuchAlertException, Unit] =
    IO.fromEither(ss.dismiss)

  def accept: IO[NoSuchAlertException, Unit] =
    IO.fromEither(ss.accept)

  def alertText: IO[NoSuchAlertException, Option[String]] =
    IO.fromEither(ss.alertText)

  def setAlertText(text: String): IO[SendAlertTextException, Unit] =
    IO.fromEither(ss.setAlertText(text))

}

object ZeleniumSession {
  def apply(
    host: String,
    port: String,
    capabilities: Capabilities
  ): Managed[Throwable, ZeleniumSession] =
    Managed.make(
      IO.effect(
        new ZeleniumSession(
          new SeleniumSession(new SeleniumRemoteWebDriver(new URL(s"http://$host:$port"), capabilities.asSelenium))
        )
      )
    )(css => UIO.apply(css.ss.rwd.quit()))

  def newTab(
    zs: ZeleniumSession
  ): Managed[Throwable, ZeleniumSession] =
    Managed.make(zs.executeScriptSync(Script("window.open()")).map(_ => zs))(
      css => UIO.apply(css.ss.rwd.close())
    )

  def fromContextType(
    zs: ZeleniumSession,
    contextType: ContextType
  ): Managed[Throwable, ZeleniumSession] = {

    val fcct = zs.current

    def switchTo(contextType: ContextType, zs: ZeleniumSession): Task[SeleniumRemoteWebDriver] =
      IO.effect(contextType match {
        case Default           => zs.ss.rwd.switchTo.defaultContent().asInstanceOf[SeleniumRemoteWebDriver]
        case Frame(id)         => zs.ss.rwd.switchTo.frame(id).asInstanceOf[SeleniumRemoteWebDriver]
        case Parent            => zs.ss.rwd.switchTo.parentFrame().asInstanceOf[SeleniumRemoteWebDriver]
        case Window(handle, _) => zs.ss.rwd.switchTo.window(handle).asInstanceOf[SeleniumRemoteWebDriver]
      })

    Managed.make(switchTo(contextType, zs).map(_ => zs))(zs => {
      zs.ss.rwd.close()
      fcct.succeedLazy(cct => switchTo(cct, zs))
    })
  }

}
