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

sealed case class Timeout private[lunium] (
  implicitWait: Long = 0,
  pageLoad: Long = 300000,
  script: Option[Long] = Some(30000)
) {
  def copy(
    implicitWait: Long = implicitWait,
    pageLoad: Long = pageLoad,
    script: Option[Long] = script
  ): Either[InvalidArgumentException, Timeout] = Timeout(implicitWait, pageLoad, script)
}

object Timeout {

  def apply(
    implicitWait: Long = 0,
    pageLoad: Long = 300000,
    script: Option[Long] = Some(30000)
  ): Either[InvalidArgumentException, Timeout] =
    for {
      script       <- validateScript(script)
      pageLoad     <- validatePageLoad(pageLoad)
      implicitWait <- validateImplicitWait(implicitWait)
    } yield (new Timeout(implicitWait, pageLoad, script))

  def script(value: Option[Long]): Either[InvalidArgumentException, Timeout] = Timeout(script = value)

  def pageLoad(value: Long): Either[InvalidArgumentException, Timeout] = Timeout(pageLoad = value)

  def implicitWait(value: Long): Either[InvalidArgumentException, Timeout] = Timeout(implicitWait = value)

  private def validateScript(value: Option[Long]): Either[InvalidArgumentException, Option[Long]] =
    value match {
      case Some(v) =>
        if (v > -1 || v < math.pow(2, 16)) {
          Right(value)
        } else {
          Left(new InvalidArgumentException(s"script should be between 0 and 2^16-1 or None, but was : $v"))
        }
      case scala.None => Right(value)
    }

  private def validatePageLoad(value: Long): Either[InvalidArgumentException, Long] =
    if (value > -1 || value < math.pow(2, 16)) {
      Right(value)
    } else {
      Left(new InvalidArgumentException(s"pageLoad should be between 0 and 2^16-1, but was : $value"))
    }

  private def validateImplicitWait(value: Long): Either[InvalidArgumentException, Long] =
    if (value > -1 || value < math.pow(2, 16)) {
      Right(value)
    } else {
      Left(new InvalidArgumentException(s"implicitWait should be between 0 and 2^16-1, but was : $value"))
    }

}
