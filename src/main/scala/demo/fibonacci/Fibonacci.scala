package demo.fibonacci

import cats.effect.instances.all._
import cats.effect.{Async, Resource}
import cats.syntax.all._
import com.comcast.ip4s._
import trace4cats.{EntryPoint, Span, Trace}
import trace4cats.http4s.server.syntax._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server
import org.http4s.syntax.all._
import trace4cats.context.Provide

import scala.concurrent.duration._

class Fibonacci[F[_]: Trace: Async](entryPoint: EntryPoint[F])
                                   (implicit provide: Provide[F, F, Span[F]]) extends Http4sDsl[F] {

  def build: Resource[F, Server] = emberServerFor(routes.inject(entryPoint))

  private def routes: HttpRoutes[F] = HttpRoutes.of {
    case GET -> Root / IntVar(which) => Ok(fibonacci(which).map(_.toString))
  }

  private def fibonacci(n: Int): F[Int] = Trace[F].span(s"fibonacci($n)") {
    // Demonstrate setting some attributes
    Trace[F].put("demo.fib", n).flatMap { _ =>
      // Make it artificially slow to simulate some real computation
      Async[F].sleep(50.millis) >> (if (n <= 2) Async[F].pure(1)
      else (fibonacci(n - 2), fibonacci(n - 1)).parMapN(_ + _))
    }
  }

  private def emberServerFor(routes: HttpRoutes[F]): Resource[F, Server] =
    EmberServerBuilder
      .default[F]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(routes.orNotFound)
      .build
}
