package org.github.sapizhak.plist.service
import cats.effect.Sync
import kantan.csv.CsvWriter

final case class CsvWriterSyntax[F[_]: Sync, A](w: CsvWriter[A]) {

  def writeF: A => F[Unit] = a => Sync[F].delay(w.write(a))

  def closeF: F[Unit] = Sync[F].delay(w.close())

}
