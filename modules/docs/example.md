---
id: exemple
title: Exemple
---

## Quick App

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
          _      <- session.deleteCookies()
          cookie <- EitherT.fromEither[IO](Cookie("n", "a", "/", Some("google.com"), false, false, scala.None))
          _      <- session.addCookie(cookie)
          found  <- session.findCookie("n").leftWiden[LuniumException]
        } yield (found)

        res
          .map(r => { println(r); ExitCode.Success })
          .valueOr(_ => ExitCode.Error)
      })

}
