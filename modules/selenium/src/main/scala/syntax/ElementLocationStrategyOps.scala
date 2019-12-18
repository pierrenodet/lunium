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
import org.openqa.selenium.{ By => SeleniumBy }

final class ElementLocationStrategyOps(elementLocationStrategy: ElementLocationStrategy) {
  def asSelenium: SeleniumBy = elementLocationStrategy match {
    case CSS(value)     => SeleniumBy.cssSelector(value)
    case XPath(value)   => SeleniumBy.xpath(value)
    case Tag(value)     => SeleniumBy.tagName(value)
    case Partial(value) => SeleniumBy.partialLinkText(value)
    case Link(value)    => SeleniumBy.linkText(value)
  }
}

trait ToElementLocationStrategyOps {

  implicit def toElementLocationStrategyOps(
    elementLocationStrategy: ElementLocationStrategy
  ): ElementLocationStrategyOps =
    new ElementLocationStrategyOps(elementLocationStrategy)

}

object els extends ToElementLocationStrategyOps
