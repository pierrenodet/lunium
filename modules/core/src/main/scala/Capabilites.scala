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

sealed trait Platform
case object Android      extends Platform
case object Any          extends Platform
case object ElCapitain   extends Platform
case object HighSierra   extends Platform
case object Ios          extends Platform
case object Linux        extends Platform
case object Mac          extends Platform
case object Mavericks    extends Platform
case object MountainLion extends Platform
case object Sierra       extends Platform
case object SnowLeopard  extends Platform
case object Unix         extends Platform
case object Vista        extends Platform
case object Win10        extends Platform
case object Win8         extends Platform
case object Win81        extends Platform
case object Windows      extends Platform
case object Xp           extends Platform
case object Yosemite     extends Platform

case class Capabilities(
  browserName: String,
  browserVersion: String,
  platformName: Platform,
  acceptInsecureCerts: Boolean = false,
  pageLoadStrategy: Option[PageLoadStrategy] = scala.None,
  proxy: Option[Proxy] = scala.None,
  setWindowRect: Option[Boolean] = scala.None,
  timeouts: Option[Timeouts] = scala.None,
  strictFileInteractability: Boolean = false,
  unhandledPromptBehavior: UnhandledPromptBehavior = DismissAndNotify
)

object Capabilities {

  val lastchromemac: Capabilities = Capabilities("chrome", "79", Mac)

}
