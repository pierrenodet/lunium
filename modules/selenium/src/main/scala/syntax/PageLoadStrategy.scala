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
import org.openqa.selenium.{ PageLoadStrategy => SeleniumPageLoadStrategy }

final class PageLoadStrategyOps(pageLoadStrategy: PageLoadStrategy) {
  def asSelenium: SeleniumPageLoadStrategy = pageLoadStrategy match {
    case Eager  => SeleniumPageLoadStrategy.EAGER
    case None   => SeleniumPageLoadStrategy.NONE
    case Normal => SeleniumPageLoadStrategy.NORMAL
  }
}

trait ToPageLoadStrategyOps {

  implicit def toPageLoadStrategyOps(pageLoadStrategy: PageLoadStrategy): PageLoadStrategyOps =
    new PageLoadStrategyOps(pageLoadStrategy)

}

object pls extends ToPageLoadStrategyOps
