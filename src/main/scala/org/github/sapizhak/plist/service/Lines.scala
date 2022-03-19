package org.github.sapizhak.plist.service
import eu.timepit.refined.numeric.Positive
import eu.timepit.refined.refineV
import eu.timepit.refined.types.all.PosInt

final case class Lines(extract: PosInt)

object Lines {

  import cats.syntax.all._

  def unapply(str: String): Option[PosInt] =
    str.startsWith(":").guard[Option] *>
      str.replace(":", "").toIntOption.flatMap(refineV[Positive](_).toOption)

}
