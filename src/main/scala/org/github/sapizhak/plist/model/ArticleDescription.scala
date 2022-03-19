package org.github.sapizhak.plist.model
import doobie.Put
import eu.timepit.refined.types.string.NonEmptyString

final case class ArticleDescription(extract: NonEmptyString)

object ArticleDescription {

  import doobie.refined.implicits._
  import kantan.csv._
  import kantan.csv.refined._

  implicit val ArticleDescriptionCellDecoder: CellDecoder[ArticleDescription] =
    CellDecoder[NonEmptyString].map(ArticleDescription.apply)

  implicit val ArticleDescriptionCellEncoder: CellEncoder[ArticleDescription] =
    CellEncoder[NonEmptyString].contramap(_.extract)

  implicit val ArticleDescriptionPut: Put[ArticleDescription] =
    Put[NonEmptyString].contramap(_.extract)

}
