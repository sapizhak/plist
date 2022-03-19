package org.github.sapizhak.plist.config

import pureconfig.ConfigReader

import java.util.Properties
import scala.jdk.CollectionConverters._

final case class PgProperties(extract: Map[String, AnyRef]) {

  lazy val jProperties: Properties = {
    val ps = new Properties()
    extract.foreach { case (k, v) => ps.put(k, v) }
    ps
  }

}

object PgProperties {

  implicit val crPgProperties: ConfigReader[PgProperties] =
    ConfigReader.fromCursor {
      _.asObjectCursor.map {
        _.objValue.toConfig
          .entrySet()
          .asScala
          .map { entry =>
            entry.getKey -> entry.getValue.unwrapped()
          }
          .toMap
      }
        .map(PgProperties.apply)
    }

}
