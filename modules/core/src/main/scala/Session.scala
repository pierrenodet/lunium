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

final case class SessionStatus(ready: Boolean, message: String)

final case class SessionId(value: String)

trait Session[F[_, _]]
    extends Fancy[F]
    with Search[F]
    with Document[F]
    with Execution[F]
    with Bakery[F]
    with Context[F] {

  def timeout: F[Nothing, Timeout]
  def setTimeout(timeout: Timeout): F[Nothing, Unit]

  def navigate(command: NavigationCommand): F[NavigationException, Unit]

  def to(url: Url): F[NavigationException, Unit] = navigate(url)
  def back: F[NavigationException, Unit]         = navigate(Back)
  def forward: F[NavigationException, Unit]      = navigate(Forward)
  def refresh: F[NavigationException, Unit]      = navigate(Refresh)

  def url: F[Nothing, Url]
  def title: F[Nothing, String]

  def contexts: F[Nothing, List[ContextType]]
  def current: F[Nothing, ContextType]

}

object Session {

  def apply[F[_, _]](implicit instance: Session[F]): Session[F] = instance

}
