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

case class PageSource(value: String)

trait Document[F[_, _]] {

  def source: F[Throwable, PageSource]

}

object Document {

  def apply[F[_, _]](implicit instance: Document[F]): Document[F] = instance

}

case class Script(value: String)

sealed trait ExecutionMode
case object SyncExecution  extends ExecutionMode
case object AsyncExecution extends ExecutionMode

trait Execution[F[_, _]] {

  def executeScript(script: Script, executionMode: ExecutionMode = SyncExecution): F[Throwable, String]

  def executeScriptSync(script: Script): F[Throwable, String]  = executeScript(script, SyncExecution)
  def executeScriptAsync(script: Script): F[Throwable, String] = executeScript(script, AsyncExecution)

}

object Execution {

  def apply[F[_, _]](implicit instance: Execution[F]): Execution[F] = instance

}
