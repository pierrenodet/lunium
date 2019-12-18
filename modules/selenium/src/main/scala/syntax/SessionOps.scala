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
import org.openqa.selenium.remote.{ SessionId => SeleniumSessionId }

final class SessionIdOps(sessionId: SessionId) {

  def asSelenium = new SeleniumSessionId(sessionId.value)

}

trait ToSessionIdOps {

  implicit def toSessionIdOps(sessionId: SessionId): SessionIdOps =
    new SessionIdOps(sessionId)

}

final class SeleniumSessionIdOps(seleniumSessionId: SeleniumSessionId) {

  def asLunium = SessionId(seleniumSessionId.toString)

}

trait ToSeleniumSessionIdOps {

  implicit def toSeleniumSessionIdOps(seleniumSessionId: SeleniumSessionId): SeleniumSessionIdOps =
    new SeleniumSessionIdOps(seleniumSessionId)

}

object session extends ToSessionIdOps with ToSeleniumSessionIdOps
