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
import lunium.selenium.implicits._
import org.openqa.selenium.{ Capabilities => SeleniumCapabilities }
import org.openqa.selenium.remote.{
  DesiredCapabilities => SeleniumDesiredCapabilities,
  CapabilityType => SeleniumCapabilityType
}

final class CapabilitiesOps(capabilities: Capabilities) {

  def asSelenium: SeleniumCapabilities = {
    val smc = new SeleniumDesiredCapabilities()
    smc.setBrowserName(capabilities.browserName)
    smc.setVersion(capabilities.browserVersion)
    smc.setPlatform(capabilities.platformName.asSelenium)
    smc.setAcceptInsecureCerts(capabilities.acceptInsecureCerts)
    capabilities.pageLoadStrategy.foreach(
      pls => smc.setCapability(SeleniumCapabilityType.PAGE_LOAD_STRATEGY, pls)
    )
    capabilities.proxy.foreach(p => smc.setCapability(SeleniumCapabilityType.PROXY, p.asSelenium))
    smc.setCapability(
      SeleniumCapabilityType.UNEXPECTED_ALERT_BEHAVIOUR,
      capabilities.unhandledPromptBehavior.asSelenium
    )
    smc
  }

}

trait ToCapabilitiesOps {

  implicit def toCapabilitiesOps(capabilities: Capabilities): CapabilitiesOps =
    new CapabilitiesOps(capabilities)

}

object capabilities extends ToCapabilitiesOps
