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

case class Cookie private[lunium] (
  name: String,
  value: String,
  path: String = "/",
  domain: Option[String] = scala.None,
  secure: Boolean = false,
  httpOnly: Boolean = false,
  expiry: Option[Long] = scala.None
){
  def copy(
    name: String=name,
    value: String=value,
    path: String =path,
    domain: Option[String] = domain,
    secure: Boolean = secure,
    httpOnly: Boolean = httpOnly,
    expiry: Option[Long] = expiry
  ): Either[InvalidArgumentException, Cookie] = Cookie(
    name,
    value,
    path,
    domain,
    secure,
    httpOnly,
    expiry
  )
}


object Cookie {

  private def validateExpiry(value: Option[Long]): Either[InvalidArgumentException, Option[Long]] =
    value match {
      case Some(value) =>
        if (value < 0) Left(new InvalidArgumentException(s"expiry should be more than 0 but it was : $value"))
        else Right(Some(value))
      case scala.None => Right(scala.None)
    }

  private def validateDomain(value: Option[String]): Either[InvalidArgumentException, Option[String]] =
    Right(value.map(d => if (d.startsWith(".")) d.tail else d))

  def apply(
    name: String,
    value: String,
    path: String = "/",
    domain: Option[String] = scala.None,
    secure: Boolean = false,
    httpOnly: Boolean = false,
    expiry: Option[Long] = scala.None
  ): Either[InvalidArgumentException, Cookie] =
    for {
      domain <- validateDomain(domain)
      expiry <- validateExpiry(expiry)
    } yield (new Cookie(
      name,
      value,
      path,
      domain.map(d => if (d.startsWith(".")) d.tail else d),
      secure,
      httpOnly,
      expiry
    ))

}

trait Bakery[F[_, _]] {

  def cookies: F[Nothing, List[Cookie]]

  def findCookie(name: String): F[NoSuchCookieException, Cookie]

  def addCookie(cookie: Cookie): F[InvalidCookieDomainException, Unit]

  def deleteCookie(name: String): F[Nothing, Unit]

  def deleteCookies(): F[Nothing, Unit]

}
