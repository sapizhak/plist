package org.github.sapizhak.plist.model
import eu.timepit.refined.types.string.NonEmptyString

final case class ArticleName(extract: NonEmptyString)

object ArticleName {

  import kantan.csv._
  import kantan.csv.refined._

  implicit val ArticleNameCellDecoder: CellDecoder[ArticleName] =
    CellDecoder[NonEmptyString].map(ArticleName.apply)

  implicit val ArticleNameCellEncoder: CellEncoder[ArticleName] =
    CellEncoder[NonEmptyString].contramap(_.extract)

}
