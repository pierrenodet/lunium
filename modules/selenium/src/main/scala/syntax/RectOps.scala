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

import org.openqa.selenium.{ Rectangle => SeleniumRectangle, Point => SeleniumPoint, Dimension => SeleniumDimension }
import lunium._

final class RectOps(rect: Rect) {

  def asSelenium: SeleniumRectangle = rect match {
    case Rect(x, y, width, height) =>
      new SeleniumRectangle(x.toInt, y.toInt, height.toInt, width.toInt)
  }

  def asSeleniumPoint: SeleniumPoint = rect match {
    case Rect(x, y, _, _) =>
      new SeleniumPoint(x.toInt, y.toInt)
  }

  def asSeleniumDimension: SeleniumDimension = rect match {
    case Rect(_, _, width, height) =>
      new SeleniumDimension(height.toInt, width.toInt)
  }

}

trait ToRectOps {

  implicit def toRectOps(rect: Rect): RectOps =
    new RectOps(rect)

}

final class SeleniumRectangeOps(seleniumRectangle: SeleniumRectangle) {

  def asLunium =
    new Rect(
      seleniumRectangle.x,
      seleniumRectangle.y,
      seleniumRectangle.width,
      seleniumRectangle.height
    )

}

trait ToSeleniumRectangeOps {

  implicit def toSeleniumRectangeOps(seleniumRectangle: SeleniumRectangle): SeleniumRectangeOps =
    new SeleniumRectangeOps(seleniumRectangle)

}
object rect extends ToRectOps with ToSeleniumRectangeOps
