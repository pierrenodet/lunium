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

import org.openqa.selenium.{ OutputType => SeleniumOutputType, Rectangle => SeleniumRectangle }
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
  ): IO[Throwable, Option[ZeleniumElement]] =
    IO.effect(
      Option(rwd.findElement(elementLocationStrategy.asSelenium))
        .map(new ZeleniumElement(_))
    )

  def findElements(
    elementLocationStrategy: ElementLocationStrategy
  ): IO[Throwable, List[ZeleniumElement]] =
    IO.effect(
      rwd
        .findElements(elementLocationStrategy.asSelenium)
        .asScala
        .toList
        .map(new ZeleniumElement(_))
    )

  def screenshot: IO[Throwable, Array[Byte]] = IO.effect(rwd.getScreenshotAs(SeleniumOutputType.BYTES))

  def timeout: IO[Nothing, Timeout] = UIO.succeed(Timeout(1))
  def setTimeout(timeout: Timeout): IO[Throwable, Unit] =
    for {
      _ <- IO.effect(
            rwd.manage.timeouts.implicitlyWait(timeout.implicitWait, TimeUnit.MILLISECONDS)
          )
      _ <- IO.effect(
            rwd.manage.timeouts.pageLoadTimeout(timeout.pageLoad, TimeUnit.MILLISECONDS)
          )
      _ <- IO.effect(
            rwd.manage.timeouts
              .setScriptTimeout(timeout.script.getOrElse(-1), TimeUnit.MILLISECONDS)
          )
    } yield ()

  def navigate(command: NavigationCommand): IO[Throwable, Unit] = IO.effect {
    command match {
      case Forward    => rwd.navigate().forward()
      case Url(value) => rwd.navigate().to(value)
      case Back       => rwd.navigate().back()
      case Refresh    => rwd.navigate().refresh()
    }
  }

  val url: IO[Nothing, String]   = UIO.apply(rwd.getCurrentUrl)
  val title: IO[Nothing, String] = UIO.apply(rwd.getTitle)

  def contexts: IO[Throwable, List[ContextType]] =
    IO.effect(rwd.getWindowHandles.asScala.toList.flatMap(ContextType.fromString))
  def current: IO[Throwable, ContextType] = IO.effect(ContextType.fromString(rwd.getWindowHandle).getOrElse(Default))

  def deleteCookies: IO[Throwable, Unit]              = IO.effect(rwd.manage().deleteAllCookies())
  def addCookie(cookie: Cookie): IO[Throwable, Unit]  = IO.effect(rwd.manage().addCookie(cookie.asSelenium))
  def cookies: IO[Throwable, List[Cookie]]            = IO.effect(rwd.manage().getCookies.asScala.map(_.asLunium).toList)
  def deleteCookie(name: String): IO[Throwable, Unit] = IO.effect(rwd.manage().deleteAllCookies())
  def findCookie(name: String): IO[Throwable, Option[Cookie]] =
    IO.effect(Try(rwd.manage().getCookieNamed(name)).toOption.map(_.asLunium))

  def executeSync(script: Script): IO[Throwable, String]  = IO.effect(rwd.executeScript(script.value).toString)
  def executeAsync(script: Script): IO[Throwable, String] = IO.effect(rwd.executeAsyncScript(script.value).toString)

  def source: IO[Throwable, PageSource] = IO.effect(PageSource(rwd.getPageSource))

  def resize(rect: Rect): IO[Throwable, Unit] = IO.effect {
    rwd.manage().window().setSize(rect.asSeleniumDimension)
    rwd.manage().window().setPosition(rect.asSeleniumPoint)
  }

  def setState(windowState: WindowState): IO[Throwable, Unit] =
    IO.effect(windowState match {
      case FullScreen => rwd.manage().window().fullscreen()
      case Maximized  => rwd.manage().window().maximize()
      case Minimized  => rwd.manage().window().setPosition(Rect(0, -1000, 0, 0).asSeleniumPoint);
    })

  val rect: IO[Nothing, Rect] =
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

    def switchTo(contextType: ContextType, zs: ZeleniumSession) =
      IO.effect(contextType match {
        case Default           => zs.rwd.switchTo.defaultContent()
        case Frame(id)         => zs.rwd.switchTo.frame(id)
        case Parent            => zs.rwd.switchTo.parentFrame()
        case Window(handle, _) => zs.rwd.switchTo.window(handle)
      })

    Managed.make(switchTo(contextType, zs).map(_ => zs))(zs => {
      zs.rwd.close()
      fcct.succeedLazy(cct => switchTo(cct, zs))
    })
  }

}
