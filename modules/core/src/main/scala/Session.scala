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
    with Context[F]
    with Navigate[F]
    with Timeout[F]
    with Alert[F]

object Session {

  def apply[F[_, _]](implicit instance: Session[F]): Session[F] = instance

}
