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

package lunium.laws

import org.openqa.selenium.{ OutputType => SeleniumOutputType, Rectangle => SeleniumRectangle }
import org.openqa.selenium.remote.{ RemoteWebDriver => SeleniumRemoteWebDriver }
import java.util.concurrent.TimeUnit
import scala.jdk.CollectionConverters._
import java.net.URL
import lunium._
import lunium.selenium.implicits._
import cats.Id
import cats.effect._
import cats.implicits._
import scala.util.Try
import cats.data.EitherT
/*
class TestSession(private[lunium] val rwd: SeleniumRemoteWebDriver) extends Session[Id] {

  def findElement(
    elementLocationStrategy: ElementLocationStrategy
  ): Id[Option[TestElement]] = Option(rwd.findElement(elementLocationStrategy.asSelenium))
        .map(new TestElement(_))

  def findElements(
    elementLocationStrategy: ElementLocationStrategy
  ): Id[List[TestElement]] =

      rwd
        .findElements(elementLocationStrategy.asSelenium)
        .asScala
        .toList
        .map(new TestElement(_))


  def screenshot: Id[Array[Byte]] = rwd.getScreenshotAs(SeleniumOutputType.BYTES)

  def timeout: Id[Timeout] = Timeout(1)
  def setTimeout(timeout: Timeout): Id[Unit] =
    {rwd.manage.timeouts.implicitlyWait(timeout.implicitWait, TimeUnit.MILLISECONDS)
      rwd.manage.timeouts.pageLoadTimeout(timeout.pageLoad, TimeUnit.MILLISECONDS)
rwd.manage.timeouts
              .setScriptTimeout(timeout.script.getOrElse(-1), TimeUnit.MILLISECONDS)

    }

  def navigate(command: NavigationCommand): Id[Unit] =
    command match {
      case Forward    => rwd.navigate().forward()
      case Url(value) => rwd.navigate().to(value)
      case Back       => rwd.navigate().back()
      case Refresh    => rwd.navigate().refresh()
    }


  val url: Id[String]   = rwd.getCurrentUrl
  val title: Id[String] = rwd.getTitle

  def contexts: Id[List[ContextType]] =
rwd.getWindowHandles.asScala.toList.flatMap(ContextType.fromString)
  def current: Id[ContextType] = ContextType.fromString(rwd.getWindowHandle).getOrElse(Default)

  def deleteCookies: Id[Unit]                      = rwd.manage().deleteAllCookies()
  def addCookie(cookie: Cookie): Id[Unit]          = rwd.manage().addCookie(cookie.asSelenium)
  def cookies: Id[List[Cookie]]                    = rwd.manage().getCookies.asScala.map(_.asLunium).toList
  def deleteCookie(name: String): Id[Unit]         = rwd.manage().deleteAllCookies()
  def findCookie(name: String): Id[Option[Cookie]] = Option(rwd.manage().getCookieNamed(name)).map(_.asLunium)

  def executeSync(script: Script): Id[String]  = rwd.executeScript(script.value).toString
  def executeAsync(script: Script): Id[String] = rwd.executeAsyncScript(script.value).toString

  def source: Id[PageSource] = PageSource(rwd.getPageSource)

  def resize(rect: Rect): Id[Unit] = {
    rwd.manage().window().setSize(rect.asSeleniumDimension)
    rwd.manage().window().setPosition(rect.asSeleniumPoint)
  }

  def setState(windowState: WindowState): Id[Unit] =
windowState match {
      case FullScreen => rwd.manage().window().fullscreen()
      case Maximized  => rwd.manage().window().maximize()
      case Minimized  => rwd.manage().window().setPosition(Rect(0, -1000, 0, 0).asSeleniumPoint);
    }

  val rect: Id[Rect] =
new SeleniumRectangle(rwd.manage().window().getPosition, rwd.manage().window().getSize).asLunium

}

object TestSession {
  def apply(
    host: String,
    port: String,
    capabilities: Capabilities
  ): TestSession =
    new TestSession(new SeleniumRemoteWebDriver(new URL(s"http://$host:$port"), capabilities.asSelenium))
}
*/
