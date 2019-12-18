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

case class Proxy(
  proxyType: ProxyType,
  proxyAutoconfigUrl: String,
  ftpProxy: String,
  httpProxy: String,
  noProxy: List[String],
  sslProxy: String,
  socksProxy: String,
  socksVersion: Int
)
