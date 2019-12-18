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
case object Default                                                            extends ContextType
case object Parent                                                             extends ContextType

sealed trait WindowState
case object Maximized  extends WindowState
case object Minimized  extends WindowState
case object FullScreen extends WindowState

object ContextType {

  def fromString(string: String): Option[ContextType] =
    if (string.startsWith("frame")) {
      Some(Frame(string))
    } else if (string.startsWith("window")) {
      Some(Window(string))
    } else {
      scala.None
    }

}

trait Context[F[_]] {

  val rect: F[Rect]

  def resize(rect: Rect): F[Unit]

  def setState(windowState: WindowState): F[Unit]

  def maximize: F[Unit] = setState(Maximized)

  def minimize: F[Unit] = setState(Minimized)

  def fullscreen: F[Unit] = setState(FullScreen)

}

object Context {

  def apply[F[_]](implicit instance: Context[F]): Context[F] = instance

}
