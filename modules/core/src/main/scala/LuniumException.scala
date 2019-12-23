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

//top level exceptions
sealed abstract class LuniumException(message: String) extends Exception(message) with Serializable

sealed trait NavigationException      extends LuniumException
sealed trait SearchElementException   extends LuniumException
sealed trait InteractElementException extends LuniumException
sealed trait SendAlertTextException   extends LuniumException

//low level exceptions
class ElementClickInterceptedException(message: String) extends LuniumException(message) with InteractElementException
class ElementNotInteractableException(message: String)
    extends LuniumException(message)
    with InteractElementException
    with SendAlertTextException

class InsecureCertificateException(message: String) extends LuniumException(message) with NavigationException

class InvalidArgumentException(message: String)     extends LuniumException(message)
class InvalidCookieDomainException(message: String) extends LuniumException(message)
class InvalidElementStateException(message: String) extends LuniumException(message) with InteractElementException
class InvalidSelectorException(message: String)     extends LuniumException(message) with SearchElementException
class InvalidSessionIdException(message: String)    extends LuniumException(message)

class JavascriptErrorException(message: String) extends LuniumException(message)

class MoveTargetOutOfBoundsException(message: String) extends LuniumException(message)

class NoSuchAlertException(message: String)   extends LuniumException(message) with SendAlertTextException
class NoSuchCookieException(message: String)  extends LuniumException(message)
class NoSuchElementException(message: String) extends LuniumException(message) with SearchElementException
class NoSuchFrameException(message: String)   extends LuniumException(message)
class NoSuchWindowException(message: String)  extends LuniumException(message)

class ScriptTimeoutException(message: String)         extends LuniumException(message)
class SessionNotCreatedException(message: String)     extends LuniumException(message)
class StaleElementReferenceException(message: String) extends LuniumException(message) with InteractElementException
class TimeoutException(message: String)               extends LuniumException(message) with NavigationException

class UnableToSetCookieException(message: String)     extends LuniumException(message)
class UnableToCaptureScreenException(message: String) extends LuniumException(message)
class UnexpectedAlertOpenException(message: String)   extends LuniumException(message)

class UnknownCommandException(message: String)       extends LuniumException(message)
class UnknownErrorException(message: String)         extends LuniumException(message)
class UnknownMethodException(message: String)        extends LuniumException(message)
class UnsupportedOperationException(message: String) extends LuniumException(message) with SendAlertTextException

class HttpUnauthorizedException(message: String) extends LuniumException(message) with NavigationException
