package org.github.sapizhak.plist.service
import cats.Applicative
import cats.effect.kernel.{Resource, Sync}
import doobie.hikari.HikariTransactor
import org.flywaydb.core.Flyway

trait FlywayMigrator[F[_]] {

  def migrateF: F[Unit]

}

object FlywayMigrator {

  import cats.syntax.all._

  private def resource[F[_]: Sync](xa: HikariTransactor[F]): Resource[F, Flyway] =
    Resource.eval {
      xa.configure { hds =>
        Sync[F].delay {
          Flyway
            .configure()
            .dataSource(hds)
            .schemas("public")
            .locations("migration")
            .load()
        }
      }
    }

  def make[F[_]: Sync: Applicative](xa: HikariTransactor[F]): Resource[F, FlywayMigrator[F]] =
    resource(xa).map { flyway =>
      new FlywayMigrator[F] {
        override def migrateF: F[Unit] =
          Sync[F].delay(flyway.info()).flatMap { a =>
            Applicative[F].whenA(a.pending().nonEmpty) {
              Sync[F]
                .delay(flyway.migrate())
                .void
            }
          }
      }
    }

}
