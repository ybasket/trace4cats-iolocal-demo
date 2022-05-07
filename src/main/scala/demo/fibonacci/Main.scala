package demo.fibonacci

import cats.effect.std.Random
import cats.effect.{ExitCode, IO, IOApp, IOLocal, Resource}
import cats.syntax.all._
import io.janstenpickle.trace4cats.Span
import io.janstenpickle.trace4cats.base.context.Provide
import io.janstenpickle.trace4cats.inject.io._
import io.janstenpickle.trace4cats.inject.{EntryPoint, Trace}
import io.janstenpickle.trace4cats.jaeger.JaegerSpanCompleter
import io.janstenpickle.trace4cats.kernel.SpanSampler
import io.janstenpickle.trace4cats.model.TraceProcess

object Main extends IOApp {
  // custom implicit random protects against https://github.com/trace4cats/trace4cats/issues/740
  override def run(args: List[String]): IO[ExitCode] = Random.scalaUtilRandom[IO].flatMap { implicit rand =>
    JaegerSpanCompleter[IO](TraceProcess("fibonacci")).use { spanCompleter =>
      Resource.eval(Span.discharge[IO](SpanSampler.always[IO], spanCompleter)).use { dischargeSpan =>
        IOLocal[Span[IO]](dischargeSpan).flatMap { ioLocal =>
          implicit val trace: Trace[IO] = ioLocalTrace(ioLocal)
          implicit val provide: Provide[IO, IO, Span[IO]] = ioLocalProvide(ioLocal)
          new Fibonacci(EntryPoint[IO](SpanSampler.always[IO], spanCompleter)).build
            .evalTap(server => IO.println(s"Server is up and running on ${server.address}"))
            .useForever
        }
      }
    }
  }
}
