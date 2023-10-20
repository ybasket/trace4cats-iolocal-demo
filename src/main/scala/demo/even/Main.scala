package demo.even

import cats.effect.std.Random
import cats.effect.{ExitCode, IO, IOApp, IOLocal, Resource}
import demo.fibonacci.Fibonacci
import trace4cats.{Span, ToHeaders}
import trace4cats.http4s.client.syntax._
import trace4cats.{EntryPoint, Trace}
import trace4cats.iolocal._
import trace4cats.jaeger.JaegerSpanCompleter
import trace4cats.kernel.SpanSampler
import trace4cats.model.TraceProcess
import org.http4s.ember.client.EmberClientBuilder
import trace4cats.context.Provide

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = Random.scalaUtilRandom[IO].flatMap { implicit rand =>
    JaegerSpanCompleter[IO](TraceProcess(args.head)).use { spanCompleter =>
      Resource.eval(Span.discharge[IO](SpanSampler.always[IO], spanCompleter)).use { dischargeSpan =>
        IOLocal[Span[IO]](dischargeSpan).flatMap { ioLocal =>
          implicit val trace: Trace[IO] = ioLocalTrace(ioLocal)
          implicit val provide: Provide[IO, IO, Span[IO]] = ioLocalProvide(ioLocal)
          val entryPoint = EntryPoint[IO](SpanSampler.always[IO], spanCompleter, ToHeaders.w3c)
          EmberClientBuilder.default[IO].build.use { client =>
            new NumbersServer[IO](entryPoint, args(1).toInt, args(2).toInt, client.liftTrace(toHeaders = ToHeaders.w3c)).build
              .evalTap(server => IO.println(s"Server is up and running on ${server.address}"))
              .useForever
          }
        }
      }
    }
  }

}
