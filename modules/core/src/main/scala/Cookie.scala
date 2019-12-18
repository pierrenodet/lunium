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

package lunium

case class Cookie(
  name: String,
  value: String,
  path: String = "/",
  domain: Option[String] = scala.None,
  secure: Boolean = false,
  httpOnly: Boolean = false,
  expiry: Option[Long] = scala.None
)

object Cookie {

  def apply(
    name: String,
    value: String,
    path: String = "/",
    domain: Option[String] = scala.None,
    secure: Boolean = false,
    httpOnly: Boolean = false,
    expiry: Option[Long] = scala.None
  ): Cookie =
    new Cookie(
      name,
      value,
      path,
      domain.map(d => if (d.startsWith(".")) d.tail else d),
      secure,
      httpOnly,
      expiry
    )

}

trait Bakery[F[_, _]] {

  def cookies: F[Throwable, List[Cookie]]

  def findCookie(name: String): F[Throwable, Option[Cookie]]

  def addCookie(cookie: Cookie): F[Throwable, Unit]

  def deleteCookie(name: String): F[Throwable, Unit]

  def deleteCookies: F[Throwable, Unit]

}
