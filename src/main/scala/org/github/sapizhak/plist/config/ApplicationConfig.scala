package org.github.sapizhak.plist.config
import cats.ApplicativeThrow
import pureconfig.ConfigSource

final case class ApplicationConfig(postgres: PgConfig, storage: StorageConfig)

object ApplicationConfig {

  import pureconfig.generic.auto._
  import eu.timepit.refined.pureconfig._

  def read[F[_]: ApplicativeThrow]: F[ApplicationConfig] =
    ApplicativeThrow[F].catchNonFatal(ConfigSource.default.loadOrThrow[ApplicationConfig])

}
