package org.github.sapizhak.plist
import cats.effect.kernel.Sync
import kantan.csv.CsvWriter

import scala.language.implicitConversions

package object service {

  implicit def csvWriterSyntax[F[_]: Sync, A](w: CsvWriter[A]): CsvWriterSyntax[F, A] =
    new CsvWriterSyntax[F, A](w)

}
