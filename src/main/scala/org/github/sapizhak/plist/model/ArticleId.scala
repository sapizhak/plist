package org.github.sapizhak.plist.model
import eu.timepit.refined.types.string.NonEmptyString

final case class ArticleId(extract: NonEmptyString)

object ArticleId {

  import kantan.csv._
  import kantan.csv.refined._

  implicit val ArticleIdCellDecoder: CellDecoder[ArticleId] =
    CellDecoder[NonEmptyString].map(ArticleId.apply)

  implicit val ArticleIdCellEncoder: CellEncoder[ArticleId] =
    CellEncoder[NonEmptyString].contramap(_.extract)

}
