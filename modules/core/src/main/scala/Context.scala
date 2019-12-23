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

sealed trait WindowType
case object Tab    extends WindowType
case object Window extends WindowType

sealed trait ContextType
case class Window(handle: String, windowType: Option[WindowType] = scala.None) extends ContextType
case class Frame(id: String)                                                   extends ContextType
case object Parent                                                             extends ContextType
case object Default                                                            extends ContextType

object ContextType {

  def fromString(string: String): Either[InvalidArgumentException, ContextType] =
    if (string.startsWith("frame")) {
      Right(Frame(string))
    } else if (string.startsWith("window")) {
      Right(Window(string))
    } else {
      Left(new InvalidArgumentException(s"string should start with frame or window but was : $string"))
    }

}

trait Context[F[_, _]] {

  def rectangle: F[Nothing, Rectangle]

  def setState(windowState: WindowState): F[UnsupportedOperationException, Unit]

  def resize(rect: Rectangle): F[UnsupportedOperationException, Unit] = setState(rect)

  def maximize: F[UnsupportedOperationException, Unit] = setState(Maximized)

  def minimize: F[UnsupportedOperationException, Unit] = setState(Minimized)

  def fullscreen: F[UnsupportedOperationException, Unit] = setState(FullScreen)

}

object Context {

  def apply[F[_, _]](implicit instance: Context[F]): Context[F] = instance

}
