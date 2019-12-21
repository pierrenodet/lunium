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

package lunium.selenium.syntax

import java.util.Date

import lunium._
import org.openqa.selenium.{ Cookie => SeleniumCookie }

final class CookieOps(cookie: Cookie) {

  def asSelenium: SeleniumCookie = {
    val cb = new SeleniumCookie.Builder(cookie.name, cookie.value)
      .isSecure(cookie.secure)
      .isHttpOnly(cookie.httpOnly)
      .path(cookie.path)
    cookie.expiry.foreach(e => cb.expiresOn(new Date(e)))
    cookie.domain.foreach(s => cb.domain(s))
    cb.build()
  }

}

trait ToCookieOps {

  implicit def toCookieOps(cookie: Cookie): CookieOps =
    new CookieOps(cookie)

}

final class SeleniumCookieOps(seleniumCookie: SeleniumCookie) {

  def asLunium: Cookie =
    new Cookie(
      seleniumCookie.getName,
      seleniumCookie.getValue,
      seleniumCookie.getPath,
      Option(seleniumCookie.getDomain),
      seleniumCookie.isSecure,
      seleniumCookie.isHttpOnly,
      Option(seleniumCookie.getExpiry).map(_.getTime)
    )

}

trait ToSeleniumCookieOps {

  implicit def toSeleniumCookieOps(seleniumCookie: SeleniumCookie): SeleniumCookieOps =
    new SeleniumCookieOps(seleniumCookie)

}

object cookie extends ToCookieOps with ToSeleniumCookieOps
