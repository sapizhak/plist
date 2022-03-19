package org.github.sapizhak.plist.service
import cats.data.EitherT
import cats.effect.kernel.Sync
import cats.{Applicative, ApplicativeThrow, Monad, MonadThrow}
import kantan.csv.ops._
import org.github.sapizhak.plist.model.Article
import org.http4s.dsl._
import org.http4s.headers.`Content-Disposition`
import org.http4s.{EntityDecoder, Headers, HttpRoutes, MalformedMessageBodyFailure, MediaRange}
import org.typelevel.ci._

import java.io.ByteArrayOutputStream

object Routes {

  import cats.syntax.all._
  import kantan.csv._

  private val RFC: CsvConfiguration =
    CsvConfiguration.rfc.withCellSeparator('|').withHeader

  implicit def productItemListEntityDecoder[F[_]: Applicative](implicit
    C: fs2.Compiler[F, F]
  ): EntityDecoder[F, List[Article]] =
    EntityDecoder.decodeBy[F, List[Article]](MediaRange.`text/*`) { ba =>
      EitherT {
        ba.body.compile
          .to(Array)
          .map { ba =>
            ba.readCsv[List, Article](RFC).sequence.leftMap { re =>
              MalformedMessageBodyFailure(re.getMessage)
            }
          }
      }
    }

  def make[F[_]: Sync: Monad: MonadThrow: ApplicativeThrow](storage: ArticlesStorage[F])(implicit
    C: fs2.Compiler[F, F],
    DSL: Http4sDsl[F]
  ): HttpRoutes[F] = {

    import DSL._

    val headers: Headers =
      Headers(`Content-Disposition`("inline", Map(ci"filename" -> "products.csv")))

    HttpRoutes
      .of[F] {
        case GET -> Root / "articles" / Lines(n) =>
          (for {
            baos <- Sync[F].delay(new ByteArrayOutputStream())
            csvw  = baos.asCsvWriter[Article](RFC)
            _    <- storage.readArticles(n).foreach(csvw.writeF).compile.drain
            _    <- csvw.closeF
          } yield baos.toByteArray).attempt.flatMap {
            case Left(_)   => InternalServerError()
            case Right(ba) => Ok(ba).map(_.withHeaders(headers))
          }
        case req @ PUT -> Root / "products" / Lines(n) =>
          req
            .as[List[Article]]
            .map(_.take(n.value))
            .redeemWith(
              _ => BadRequest(),
              items =>
                storage.putArticles(fs2.Stream.emits(items)).attempt.flatMap {
                  case Left(_)  => InternalServerError()
                  case Right(_) => Ok()
                }
            )
      }
  }

}
