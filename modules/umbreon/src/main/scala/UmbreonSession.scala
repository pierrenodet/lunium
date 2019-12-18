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

class UmbreonSession[F[_]: Sync](private[lunium] val rwd: SeleniumRemoteWebDriver) extends Session[EitherT[F, ?, ?]] {

  def screenshot: EitherT[F, Throwable, Array[Byte]] =
    EitherT.liftF(Sync[F].delay(rwd.getScreenshotAs(SeleniumOutputType.BYTES)))

  def timeout: EitherT[F, Throwable, Timeout] = EitherT.liftF(Sync[F].pure(Timeout(1)))
  def setTimeout(timeout: Timeout): EitherT[F, Throwable, Unit] =
    for {
      _ <- EitherT.liftF(
            Sync[F].delay(
              rwd.manage.timeouts.implicitlyWait(timeout.implicitWait, TimeUnit.MILLISECONDS)
            )
          )
      _ <- EitherT.liftF(
            Sync[F].delay(
              rwd.manage.timeouts.pageLoadTimeout(timeout.pageLoad, TimeUnit.MILLISECONDS)
            )
          )
      _ <- EitherT.liftF(
            Sync[F].delay(
              rwd.manage.timeouts
                .setScriptTimeout(timeout.script.getOrElse(-1), TimeUnit.MILLISECONDS)
            )
          )
    } yield ()

  def navigate(command: NavigationCommand): EitherT[F, Throwable, Unit] =
    EitherT.liftF(Sync[F].delay {
      command match {
        case Forward    => rwd.navigate().forward()
        case Url(value) => rwd.navigate().to(value)
        case Back       => rwd.navigate().back()
        case Refresh    => rwd.navigate().refresh()
      }
    })

  val url: EitherT[F, Nothing, String]   = EitherT.liftF(Sync[F].delay(rwd.getCurrentUrl))
  val title: EitherT[F, Nothing, String] = EitherT.liftF(Sync[F].delay(rwd.getTitle))

  def contexts: EitherT[F, Throwable, List[ContextType]] =
    EitherT.liftF(Sync[F].delay(rwd.getWindowHandles.asScala.toList.flatMap(ContextType.fromString)))
  def current: EitherT[F, Throwable, ContextType] =
    EitherT.liftF(Sync[F].delay(ContextType.fromString(rwd.getWindowHandle).getOrElse(Default)))

  def deleteCookies: EitherT[F, Throwable, Unit] = EitherT.liftF(Sync[F].delay(rwd.manage().deleteAllCookies()))
  def addCookie(cookie: Cookie): EitherT[F, Throwable, Unit] =
    EitherT.liftF(Sync[F].delay(rwd.manage().addCookie(cookie.asSelenium)))
  def cookies: EitherT[F, Throwable, List[Cookie]] =
    EitherT.liftF(Sync[F].delay(rwd.manage().getCookies.asScala.map(_.asLunium).toList))
  def deleteCookie(name: String): EitherT[F, Throwable, Unit] =
    EitherT.liftF(Sync[F].delay(rwd.manage().deleteAllCookies()))
  def findCookie(name: String): EitherT[F, Throwable, Option[Cookie]] =
    EitherT.liftF(Sync[F].delay(Option(rwd.manage().getCookieNamed(name)).map(_.asLunium)))

  def executeSync(script: Script): EitherT[F, Throwable, String] =
    EitherT.liftF(Sync[F].delay(rwd.executeScript(script.value).toString))
  def executeAsync(script: Script): EitherT[F, Throwable, String] =
    EitherT.liftF(Sync[F].delay(rwd.executeAsyncScript(script.value).toString))

  def source: EitherT[F, Throwable, PageSource] = EitherT.liftF(Sync[F].delay(PageSource(rwd.getPageSource)))

  def findElement(
    elementLocationStrategy: ElementLocationStrategy
  ): EitherT[F, Throwable, Option[Element[EitherT[F, ?, ?]]]] =
    EitherT.liftF(
      Sync[F].delay(
        Option(rwd.findElement(elementLocationStrategy.asSelenium))
          .map(new UmbreonElement(_))
      )
    )

  def findElements(
    elementLocationStrategy: ElementLocationStrategy
  ): EitherT[F, Throwable, List[Element[EitherT[F, ?, ?]]]] =
    EitherT.liftF(
      Sync[F].delay(
        rwd
          .findElements(elementLocationStrategy.asSelenium)
          .asScala
          .toList
          .map(new UmbreonElement(_))
      )
    )

  def resize(rect: Rect): EitherT[F, Throwable, Unit] =
    EitherT.liftF(Sync[F].delay {
      rwd.manage().window().setSize(rect.asSeleniumDimension)
      rwd.manage().window().setPosition(rect.asSeleniumPoint)
    })

  def setState(windowState: WindowState): EitherT[F, Throwable, Unit] =
    EitherT.liftF(Sync[F].delay(windowState match {
      case FullScreen => rwd.manage().window().fullscreen()
      case Maximized  => rwd.manage().window().maximize()
      case Minimized  => rwd.manage().window().setPosition(Rect(0, -1000, 0, 0).asSeleniumPoint);
    }))

  val rect: EitherT[F, Nothing, Rect] =
    EitherT.liftF(
      Sync[F].delay(new SeleniumRectangle(rwd.manage().window().getPosition, rwd.manage().window().getSize).asLunium)
    )

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
      css => Sync[F].delay(css.map(_.rwd.close()))
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
