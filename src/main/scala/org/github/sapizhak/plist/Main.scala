package org.github.sapizhak.plist
import cats.effect.kernel.Async
import cats.effect._
import org.github.sapizhak.plist.config.ApplicationConfig
import org.github.sapizhak.plist.service.{Routes, FlywayMigrator, HikariTransactor, ArticlesStorage, PrometheusRegistry}
import org.http4s.dsl.Http4sDsl
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.metrics.prometheus.Prometheus
import org.http4s.server.Server
import org.http4s.server.middleware.{GZip, Metrics}

object Main extends IOApp {

  import com.comcast.ip4s._

  def HttpServer[F[_]: Async: MonadCancelThrow](config: ApplicationConfig)(implicit
    C: fs2.Compiler[F, F],
    DSL: Http4sDsl[F]
  ): Resource[F, Server] =
    for {
      registry   <- PrometheusRegistry.make
      xa         <- HikariTransactor.make(config.postgres, registry)
      flyway     <- FlywayMigrator.make(xa)
      _          <- Resource.eval(flyway.migrateF)
      storage     = ArticlesStorage.make(xa, config.storage.maxInsert)
      rawroutes   = Routes.make(storage)
      metricsops <- Prometheus.metricsOps(registry, "products_server")
      routes      = Metrics(metricsops)(GZip(rawroutes)).orNotFound
      server <- EmberServerBuilder.default
                  .withHost(host"0.0.0.0")
                  .withPort(port"9000")
                  .withHttpApp(routes)
                  .build
    } yield server

  override def run(args: List[String]): IO[ExitCode] = {
    implicit val DSL: Http4sDsl[IO] = org.http4s.dsl.io
    ApplicationConfig
      .read[IO]
      .flatMap(HttpServer[IO](_).useForever.void)
      .as(ExitCode.Success)
  }

}
