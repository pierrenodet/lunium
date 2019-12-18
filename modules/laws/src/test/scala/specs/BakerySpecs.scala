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

package lunium.laws.specs

import cats.kernel.laws._
import cats.effect._
import cats.effect.laws.util.TestContext
import cats.effect.laws.util.TestInstances
import cats.implicits._
import cats.Id

import lunium._
import lunium.selenium.implicits._
//import lunium.laws.TestSession
import lunium.laws.arbitrary.ArbitraryCookie
import lunium.laws.discipline.BakeryTests

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest._
import org.typelevel.discipline.scalatest.Discipline
import org.scalacheck._
import java.net.URL
import org.openqa.selenium.remote.{ RemoteWebDriver => SeleniumRemoteWebDriver }
import org.openqa.selenium.chrome.{ ChromeDriver => SeleniumChromeDriver }
/*
class BakerySpecs extends AnyFunSuite with Discipline with ArbitraryCookie with TestInstances with BeforeAndAfterAll {

    var ts:TestSession = _

   override def beforeAll() {
        ts = TestSession("localhost","4444/wd/hub",Capabilities.lastchromemac)
    }

    override def afterAll() {
        ts.rwd.quit()
    }

   checkAll("Bakery", BakeryTests(ts).algebra)

}
*/
