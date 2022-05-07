package demo.even

import cats.effect.{Async, Resource}
import cats.syntax.all._
import com.comcast.ip4s.{IpLiteralSyntax, Port}
import io.circe.syntax._
import io.janstenpickle.trace4cats.Span
import io.janstenpickle.trace4cats.base.context.Provide
import io.janstenpickle.trace4cats.http4s.server.syntax._
import io.janstenpickle.trace4cats.inject._
import org.http4s.HttpRoutes
import org.http4s.client.Client
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.Http4sDsl
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server

class NumbersServer[F[_] : Trace : Async](entryPoint: EntryPoint[F], port: Int, otherPort: Int, client: Client[F])
                                         (implicit provide: Provide[F, F, Span[F]]) extends Http4sDsl[F] {

  def build: Resource[F, Server] = emberServerFor(routes.inject(entryPoint))

  private def routes: HttpRoutes[F] = HttpRoutes.of {
    case GET -> Root / "even" / IntVar(num) => Ok(even(num.abs).map(_.asJson))
    case GET -> Root / "odd" / IntVar(num) => Ok(odd(num.abs).map(_.asJson))
  }

  private def even(n: Int): F[Boolean] = { // Trace[F].span(s"even($n)")
    if (n == 0) true.pure[F] else if (n == 1) false.pure[F] else
      client.expect[Boolean](s"http://localhost:$otherPort/odd/${n - 1}")
  }

  private def odd(n: Int): F[Boolean] = { // Trace[F].span(s"odd($n)")
    if (n == 0) false.pure[F] else if (n == 1) true.pure[F] else
      client.expect[Boolean](s"http://localhost:$otherPort/even/${n - 1}")
  }

  private def emberServerFor(routes: HttpRoutes[F]): Resource[F, Server] =
    EmberServerBuilder
      .default[F]
      .withHost(ipv4"0.0.0.0")
      .withPort(Port.fromInt(port).get)
      .withHttpApp(routes.orNotFound)
      .build
}
