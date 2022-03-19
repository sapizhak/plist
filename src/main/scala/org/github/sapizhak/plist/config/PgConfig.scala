package org.github.sapizhak.plist.config
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.metrics.prometheus.PrometheusMetricsTrackerFactory
import eu.timepit.refined.types.numeric.{PosInt, PosLong}
import eu.timepit.refined.types.string.NonEmptyString

import scala.concurrent.duration.FiniteDuration
import eu.timepit.refined.auto._

final case class PgConfig(
  username: NonEmptyString,
  password: NonEmptyString,
  hosts: NonEmptyString,
  database: NonEmptyString,
  connTimeout: FiniteDuration,
  maxPoolSize: PosInt,
  idleTimeout: PosLong,
  minIdle: PosInt,
  driverClassName: NonEmptyString,
  properties: PgProperties
) {

  val jdbcUrl: String =
    s"jdbc:postgresql://${hosts.value}/${database.value}"

  val hikariConfig: PrometheusMetricsTrackerFactory => HikariConfig = tf => {
    val hc = new HikariConfig()
    hc.setJdbcUrl(jdbcUrl)
    hc.setMinimumIdle(minIdle)
    hc.setIdleTimeout(idleTimeout)
    hc.setDriverClassName(driverClassName)
    hc.setMaximumPoolSize(maxPoolSize.value)
    hc.setUsername(username.value)
    hc.setPassword(password.value)
    hc.setConnectionTimeout(connTimeout.toMillis)
    hc.setDataSourceProperties(properties.jProperties)
    hc.setMetricsTrackerFactory(tf)
    hc
  }

}
