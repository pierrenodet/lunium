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

final case class ElementId(value: String)

trait Element[F[_, _]] extends Search[F] with Fancy[F] with ElementInteraction[F] {

  def selected: F[StaleElementReferenceException, Boolean]

  def hasAttribute(name: String): F[StaleElementReferenceException, Boolean]

  def attribute(name: String): F[StaleElementReferenceException, Option[String]]

  def property(name: String): F[StaleElementReferenceException, Option[String]]

  def css(name: String): F[StaleElementReferenceException, Option[String]]

  def text(whitespace: String = "WS"): F[StaleElementReferenceException, String]

  def name: F[StaleElementReferenceException, String]

  def rectangle: F[StaleElementReferenceException, Rectangle]

  def enabled: F[StaleElementReferenceException, Boolean]

}

object Element {

  def apply[F[_, _]](implicit instance: Element[F]): Element[F] = instance

}

trait ElementInteraction[F[_, _]] {

  def click: F[InteractElementException, Unit]

  def clear: F[InteractElementException, Unit]

  def sendKeys(keys: Keys): F[InteractElementException, Unit]

}

object ElementInteraction {

  def apply[F[_, _]](implicit instance: ElementInteraction[F]): ElementInteraction[F] = instance

}
