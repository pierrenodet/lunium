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

sealed trait ProxyType
case object Pac        extends ProxyType
case object Direct     extends ProxyType
case object Autodetect extends ProxyType
case object Manual     extends ProxyType

sealed case class Proxy private[lunium](
  proxyType: ProxyType,
  proxyAutoconfigUrl: Option[String] = scala.None,
  ftpProxy: Option[String] = scala.None,
  httpProxy: Option[String] = scala.None,
  noProxy: List[String] = List.empty,
  sslProxy: Option[String] = scala.None,
  socksProxy: Option[String] = scala.None,
  socksVersion: Option[Int] = scala.None
)

object Proxy {

  def manual(
    ftpProxy: String,
    httpProxy: String,
    noProxy: List[String],
    sslProxy: String,
    socksProxy: String,
    socksVersion: Int
  ): Either[InvalidArgumentException, Proxy] =
    if (socksVersion < 0 || socksVersion > 255) {
      Left(new InvalidArgumentException(s"socksVersion should be between 0 and 255 but was : $socksVersion"))
    } else {
      Right(
        new Proxy(
          Manual,
          ftpProxy = Some(ftpProxy),
          httpProxy = Some(httpProxy),
          noProxy = noProxy,
          sslProxy = Some(sslProxy),
          socksProxy = Some(socksProxy),
          socksVersion = Some(socksVersion)
        )
      )
    }

  def pac(proxyAutoconfigUrl: String): Either[InvalidArgumentException, Proxy] =
    Right(new Proxy(Pac, proxyAutoconfigUrl = Some(proxyAutoconfigUrl)))

  val autodetect: Proxy = new Proxy(Autodetect)

  val direct: Proxy = new Proxy(Direct)

}
