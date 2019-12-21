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

sealed trait ElementLocationStrategy
case class CSS(value: String)     extends ElementLocationStrategy
case class Link(value: String)    extends ElementLocationStrategy
case class Partial(value: String) extends ElementLocationStrategy
case class Tag(value: String)     extends ElementLocationStrategy
case class XPath(value: String)   extends ElementLocationStrategy

final case class ElementId(value: String)

trait Element[F[_, _]] extends Search[F] with Fancy[F] {

  def isSelected: F[StaleElementReferenceException, Boolean]

  def hasAttribute(name: String): F[StaleElementReferenceException, Boolean]

  def attribute(name: String): F[StaleElementReferenceException, String]

  def property(name: String): F[StaleElementReferenceException, String]

  def css(name: String): F[StaleElementReferenceException, String]

  def text(whitespace: String = "WS"): F[StaleElementReferenceException, String]

  def name: F[StaleElementReferenceException, String]

  def rect: F[StaleElementReferenceException, Rect]

  def enabled: F[StaleElementReferenceException, Boolean]

  def click: F[InteractElementException, Unit]

  def clear: F[InteractElementException, Unit]

  def sendKeys(keys: Keys): F[InteractElementException, Unit]

}
