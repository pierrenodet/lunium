---
id: overview
title: Overview
---

## Why ?

I was getting pissed to get unchecked exceptions everywhere, cluncky Thread.sleep and browsers hanging opened left and right.

Hope that's clean enough for you :

```scala
UmbreonSession
  .headlessChrome
  .use(session => (
    for {
      url    <- EitherT.fromEither[IO](Url("http://google.com"))
      _      <- session.to(url)
      element <- session.findElement(XPath("""//*[@id="hplogo"]""")).leftWiden[LuniumException]
    } yield (element)
  ).value
)
```

## How it's done

I tried to make it as modular as possible, you have tagless "algebras" bifunctored and ADTs in the ```lunium-core``` module.

The ```lunium-selenium``` propose implicits conversion between the lunium ADTs and selenium objects.

Then there is one module per effect library ecosystem, ```lunium-zelenium``` for ZIO, and ```lunium-umbreon``` for cats.

## Ready to Install ?

**Core**

```scala
libraryDependencies += "com.github.pierrenodet" %% "lunium-core" % "@VERSION@"
```

**Choose your Monad**

```scala
libraryDependencies += "com.github.pierrenodet" %% "lunium-zelenium" % "@VERSION@"
libraryDependencies += "com.github.pierrenodet" %% "lunium-umbreon" % "@VERSION@"
```
