package org.github.sapizhak.plist.service
import cats.FlatMap
import cats.effect.kernel.{Async, Resource}
import com.zaxxer.hikari.metrics.prometheus.PrometheusMetricsTrackerFactory
import doobie.ExecutionContexts
import io.prometheus.client.CollectorRegistry
import org.github.sapizhak.plist.config.PgConfig

object HikariTransactor {

  def make[F[_]: Async: FlatMap](
    config: PgConfig,
    registry: CollectorRegistry
  ): Resource[F, doobie.hikari.HikariTransactor[F]] =
    for {
      ec <- ExecutionContexts.cachedThreadPool[F]
      tf  = new PrometheusMetricsTrackerFactory(registry)
      xa <- doobie.hikari.HikariTransactor.fromHikariConfig[F](config.hikariConfig(tf), ec)
    } yield xa

}
