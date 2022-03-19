package org.github.sapizhak.plist.service
import cats.effect.kernel.{Resource, Sync}
import io.prometheus.client.CollectorRegistry

object PrometheusRegistry {

  def make[F[_]: Sync]: Resource[F, CollectorRegistry] =
    Resource.pure(new CollectorRegistry())

}
