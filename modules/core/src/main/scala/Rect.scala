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

sealed case class Rect private[lunium] (x: Long, y: Long, width: Long, height: Long) {
  def copy(
    x: Long = x,
    y: Long = y,
    width: Long = width,
    height: Long = height
  ): Either[InvalidArgumentException, Rect] = Rect(x, y, width, height)
}

object Rect {

  def apply(x: Long, y: Long, width: Long, height: Long): Either[InvalidArgumentException, Rect] =
    for {
      x      <- validateXY(x, "x")
      y      <- validateXY(y, "y")
      width  <- validateWidthHeight(width, "width")
      height <- validateWidthHeight(height, "height")
    } yield (new Rect(x, y, width, height))

  private def validateXY(value: Long, name: String): Either[InvalidArgumentException, Long] =
    if ((value > -math.pow(2, 31) - 1) && (value < math.pow(2, 31))) Right(value)
    else
      Left(
        new InvalidArgumentException(
          s"$name should be between ${-math.pow(2, 31)} and ${math.pow(2, 31) - 1}, but was : $value"
        )
      )

  private def validateWidthHeight(value: Long, name: String): Either[InvalidArgumentException, Long] =
    if ((value > -1) && (value < math.pow(2, 31))) Right(value)
    else
      Left(
        new InvalidArgumentException(s"$name should be between ${0} and ${math.pow(2, 31) - 1}, but was : $value")
      )

}
