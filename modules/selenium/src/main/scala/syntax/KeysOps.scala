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

import lunium._
import org.openqa.selenium.{ Keys => SeleniumKeys }
import scala.jdk.CollectionConverters._

final class KeysOps(keys: Keys) {
  def asSelenium: String = {
    val altSeleniumKey = if (keys.alt) Some(SeleniumKeys.ALT) else Option.empty[SeleniumKeys]
    val shiftSeleniumKey =
      if (keys.shift) Some(SeleniumKeys.SHIFT) else Option.empty[SeleniumKeys]
    val ctrlSeleniumKey =
      if (keys.ctrl) Some(SeleniumKeys.CONTROL) else Option.empty[SeleniumKeys]
    val metaSeleniumKey    = if (keys.meta) Some(SeleniumKeys.META) else Option.empty[SeleniumKeys]
    val pressedSeleniumKey = keys.pressed.toSeq.map(SeleniumKeys.valueOf)
    SeleniumKeys.chord(
      (pressedSeleniumKey ++ metaSeleniumKey ++ ctrlSeleniumKey ++ shiftSeleniumKey ++ altSeleniumKey)
        .map(_.asInstanceOf[CharSequence])
        .asJava
    )
  }
}

trait ToKeysOps {

  implicit def toKeysOps(keys: Keys): KeysOps =
    new KeysOps(keys)

}

object keys extends ToKeysOps
