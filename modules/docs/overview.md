---
id: overview
title: Overview
---

## Why ?

I was doing some projects about web scrapping, and I was tired of the selenium Java API, getting unchecked exceptions everywhere, cluncky sleeps and waits, and browsers hanging opened left and right when something bad happen.

So I have decided to actually apply all the blogs I have read about why FP is great, pure functions, effects, ADT, tagless final (don't kill me it's the double coffee cup : ```F[_,_]```), ...

The aim of the project is to go as far as skunk with reimpleting a complete client for WebDrivers, but as it's though and i wanted to at least get the API right, it's using Selenium as the first implementation of my "algebras".

Here is small snippet, hope it's good enough, I'm a shitty programmer :

```scala
UmbreonSession
  .headlessChrome
  .use(session => (
    for {
      url    <- EitherT.fromEither[IO](Url("http://google.com"))
      _      <- session.to(url)
      element <- session.findElement(XPath("""//*[@id="hplogo"]"""))
      imageLink <- element.attribute("src").leftWiden[LuniumException]
    } yield (imageLink)
  ).value
)
```

## What are this modules ?

I tried to make it as modular as possible, you have tagless "algebras" bifunctored and ADTs in the ```lunium-core``` module.

The ```lunium-selenium``` propose implicits conversion between the lunium ADTs and selenium objects, and an interpreter for selenium without effect types (but it's unusable).

Then there is one module per effect library ecosystem, ```lunium-zelenium``` for ZIO, and ```lunium-umbreon``` for cats. They both contain an interpreter that are not interdependent and hopefully leverage the best of both ecosystems, at the price of small copy pasting.

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
