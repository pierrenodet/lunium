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

import lunium._
import org.openqa.selenium.{ Proxy => SeleniumProxy }
import lunium.selenium.implicits._

final class ProxyTypeOps(proxyType: ProxyType) {
  def asSelenium: SeleniumProxy.ProxyType = proxyType match {
    case Autodetect => SeleniumProxy.ProxyType.AUTODETECT
    case Direct     => SeleniumProxy.ProxyType.DIRECT
    case Manual     => SeleniumProxy.ProxyType.MANUAL
    case Pac        => SeleniumProxy.ProxyType.PAC
  }
}

trait ToProxyTypeOps {

  implicit def toProxyTypeOps(proxyType: ProxyType): ProxyTypeOps =
    new ProxyTypeOps(proxyType)

}

final class ProxyOps(proxy: Proxy) {
  def asSelenium: SeleniumProxy = {
    var res = new SeleniumProxy()
      .setProxyType(proxy.proxyType.asSelenium)
    res = proxy.proxyAutoconfigUrl.fold(res)(res.setProxyAutoconfigUrl(_))
    res = proxy.ftpProxy.fold(res)(res.setFtpProxy(_))
    res = proxy.httpProxy.fold(res)(res.setHttpProxy(_))
    res = proxy.sslProxy.fold(res)(res.setSslProxy(_))
    res = proxy.socksProxy.fold(res)(res.setSocksProxy(_))
    res = proxy.socksVersion.fold(res)(res.setSocksVersion(_))
    proxy.noProxy.foldLeft(res) { case (inter, np) => inter.setNoProxy(np) }
  }
}

trait ToProxyOps {

  implicit def toProxyOps(proxy: Proxy): ProxyOps =
    new ProxyOps(proxy)

}

object proxy extends ToProxyTypeOps with ToProxyOps
