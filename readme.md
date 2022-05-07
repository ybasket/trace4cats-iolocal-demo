# Demos for `IOLocal`-based tracing with trace4cats

A small sample web service that calculates Fibonacci numbers using an inefficient, but parallel algorithm while producing
spans. Demonstrates how tracing can be implemented using `cats.effect.IOLocal` and how the problem of root span creation could be handled (approach not tested yet).

Also included is a second example of a service that determines whether a number is even or odd by performing a mutual recursion with a twin service instance, decreasing the number by 1 each time.
This demonstrates how tracing information can be passed and received via HTTP (using the W3C `traceparent` header).

## Usage

### Preparation

The code requires a trace4cats dependency that includes the PR for `IOLocal`: https://github.com/trace4cats/trace4cats/pull/729.
Sonatype snapshots from `0.13.1+20-973c3271` on fulfil this requirement. If you want to test further developments, make sure to publish locally and replace the version in `build.sbt`.

### Run the Fibonacci service

To run the service, use `sbt`:

```shell
sbt run
```

As this will not trigger any action other than starting the service and binding it to port 8080, request it via curl:

```shell
curl http://localhost:8080/6
```

The last path segment is the index of the Fibonacci number to calculate. Careful, higher numbers will create a huge amount of spans
and take a while to complete (the computation is slowed down by sleeping in between to provide a real-world trace graph).

### Run the even/odd services

To run the two services, open two shells and run

```shell
sbt runMain demo.even.Main one 8081 8082
```

and

```shell
sbt runMain demo.even.Main one 8082 8081
```

Then you can curl the first service like this

```shell
curl http://localhost:8080/even/6
```

It will check whether 6 is a recursion anchor (0 or 1) and if not, decrease by 1 one call the other service instance, that will perform the same and so on until an anchor is reached.

## See the results

The service uses the Jaeger integration, so spin up a local instance to see the traces produced. See [https://www.jaegertracing.io/docs/1.33/getting-started/](https://www.jaegertracing.io/docs/1.33/getting-started/) on how to do that, then open [http://localhost:16686](http://localhost:16686) in your browser.

Traces produced by the Fibonacci service will then look like this:

![Jaeger screenshot](docs/screenshot-jaeger.png)
