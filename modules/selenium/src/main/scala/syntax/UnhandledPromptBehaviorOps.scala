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
import org.openqa.selenium.{ UnexpectedAlertBehaviour => SeleniumUnexpectedAlertBehaviour }

final class UnhandledPromptBehaviorOps(unhandledPromptBehavior: UnhandledPromptBehavior) {
  def asSelenium: SeleniumUnexpectedAlertBehaviour = unhandledPromptBehavior match {
    case Ignore           => SeleniumUnexpectedAlertBehaviour.IGNORE
    case Accept           => SeleniumUnexpectedAlertBehaviour.ACCEPT
    case AcceptAndNotify  => SeleniumUnexpectedAlertBehaviour.ACCEPT_AND_NOTIFY
    case Dismiss          => SeleniumUnexpectedAlertBehaviour.DISMISS
    case DismissAndNotify => SeleniumUnexpectedAlertBehaviour.DISMISS_AND_NOTIFY
  }
}

trait ToUnhandledPromptBehaviorOps {

  implicit def toUnhandledPromptBehaviorOps(
    unhandledPromptBehavior: UnhandledPromptBehavior
  ): UnhandledPromptBehaviorOps =
    new UnhandledPromptBehaviorOps(unhandledPromptBehavior)

}

object upb extends ToUnhandledPromptBehaviorOps
