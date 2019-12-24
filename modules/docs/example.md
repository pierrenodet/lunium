---
id: exemple
title: Exemple
---

## Quick App

Here is what would like an app that open a session from a remote WebDriver server, then would go on google.com, find the logo and get back the link of the image. If there is no link or an error appened, the app would fail, otherwise it will print it into the console.

```scala
import cats.effect._
import lunium._
import lunium.umbreon._
import cats.data.EitherT
import cats.implicits._

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    UmbreonSession
      .fromCapabilities[IO]("localhost", "9515", Capabilities.lastchromemac)
      .use(session => {
        val res = for {
          url    <- EitherT.fromEither[IO](Url("http://google.com"))
          _      <- session.to(url)
          element <- session.findElement(XPath("""//*[@id="hplogo"]"""))
          imageLink <- element.attribute("src").leftWiden[LuniumException]
        } yield (imageLink)
        ).value

        res
          .map{
            case Some(value) =>
              println(value)
              ExitCode.Success
            case None =>
              ExitCode.Error
          }
          .valueOr(_ => ExitCode.Error)
      })

}
