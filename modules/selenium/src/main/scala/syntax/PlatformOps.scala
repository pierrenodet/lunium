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
import org.openqa.selenium.{ Platform => SeleniumPlatform }

final class PlatformOps(platform: Platform) {

  def asSelenium: SeleniumPlatform = platform match {
    case Unix         => SeleniumPlatform.UNIX
    case MountainLion => SeleniumPlatform.MOUNTAIN_LION
    case Yosemite     => SeleniumPlatform.YOSEMITE
    case Ios          => SeleniumPlatform.IOS
    case HighSierra   => SeleniumPlatform.HIGH_SIERRA
    case SnowLeopard  => SeleniumPlatform.SNOW_LEOPARD
    case Xp           => SeleniumPlatform.XP
    case Win81        => SeleniumPlatform.WIN8_1
    case Mac          => SeleniumPlatform.MAC
    case ElCapitain   => SeleniumPlatform.EL_CAPITAN
    case Sierra       => SeleniumPlatform.SIERRA
    case Windows      => SeleniumPlatform.WINDOWS
    case Any          => SeleniumPlatform.ANY
    case Android      => SeleniumPlatform.ANDROID
    case Linux        => SeleniumPlatform.LINUX
    case Mavericks    => SeleniumPlatform.MAVERICKS
    case Vista        => SeleniumPlatform.VISTA
    case Win10        => SeleniumPlatform.WIN10
    case Win8         => SeleniumPlatform.WIN8
  }

}

trait ToPlatformOps {

  implicit def toPlatformOps(platform: Platform): PlatformOps =
    new PlatformOps(platform)

}

object platform extends ToPlatformOps
