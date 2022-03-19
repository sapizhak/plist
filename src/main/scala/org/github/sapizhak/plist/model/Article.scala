package org.github.sapizhak.plist.model
import eu.timepit.refined.types.all.{NonNegInt, PosFloat}
import eu.timepit.refined.types.string.NonEmptyString

final case class Article(
  id: NonEmptyString,
  productId: ArticleId,
  name: ArticleName,
  description: Option[ArticleDescription],
  price: PosFloat,
  stock: NonNegInt
)

object Article {

  import kantan.csv._
  import kantan.csv.refined._

  implicit val ArticleHeaderDecoder: HeaderDecoder[Article] =
    HeaderDecoder.decoder("id", "produktId", "name", "beschreibung", "preis", "bestand")(Article.apply)

  implicit val ArticleHeaderEncoder: HeaderEncoder[Article] =
    HeaderEncoder.caseEncoder("id", "produktId", "name", "beschreibung", "preis", "bestand")(Article.unapply)

}
