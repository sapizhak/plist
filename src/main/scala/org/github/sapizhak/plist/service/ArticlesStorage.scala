package org.github.sapizhak.plist.service
import cats.data.NonEmptyList
import cats.effect.kernel.MonadCancelThrow
import doobie.hikari.HikariTransactor
import doobie.util.log.LogHandler
import eu.timepit.refined.types.all.PosInt
import org.github.sapizhak.plist.model.Article

trait ArticlesStorage[F[_]] {

  val readArticles: PosInt => fs2.Stream[F, Article]

  val putArticles: fs2.Stream[F, Article] => F[Unit]

}

object ArticlesStorage {

  import cats.syntax.all._
  import doobie.implicits._
  import doobie.refined.implicits._
  import eu.timepit.refined.auto._

  def make[F[_]: MonadCancelThrow](
    xa: HikariTransactor[F],
    MaxInsertable: PosInt
  )(implicit C: fs2.Compiler[F, F]): ArticlesStorage[F] = new ArticlesStorage[F] {

    implicit val LH: LogHandler = LogHandler.jdkLogHandler

    override val readArticles: PosInt => fs2.Stream[F, Article] = limit =>
      sql"""WITH 
            ts AS (
               SELECT
               SUM(stock) total_stock, product_id
               FROM articles p
               GROUP BY product_id
               HAVING SUM(stock) > 0
            ),
            mp AS (
               SELECT 
               DISTINCT ON (price) 
               id, p.product_id, name, description, price min_price
               FROM articles p 
               JOIN ts ON p.product_id = ts.product_id
               WHERE price IN (SELECT MIN(price) FROM articles WHERE product_id = ts.product_id)
            )
            SELECT mp.id, mp.product_id, mp.name, mp.description, mp.min_price, ts.total_stock 
            FROM articles p 
            JOIN ts ON ts.product_id = p.product_id 
            JOIN mp ON mp.product_id = p.product_id
            WHERE p.id = mp.id
            LIMIT $limit;"""
        .query[Article]
        .stream
        .transact(xa)

    private val insertProductItemsBatch: NonEmptyList[Article] => doobie.ConnectionIO[Unit] = nel => {
      val valuesFragment = nel.map { case Article(id, productId, name, description, price, stock) =>
        fr0"($id, $productId, $name, $description, $price, $stock)"
      }.intercalate(fr0",")
      sql"""INSERT INTO articles (id, product_id, name, description, price, stock) 
            VALUES $valuesFragment
            ON CONFLICT (id, product_id)
            DO UPDATE 
            SET stock = EXCLUDED.stock;""".update.run.void
    }

    override val putArticles: fs2.Stream[F, Article] => F[Unit] = stream =>
      stream
        .chunkN(MaxInsertable)
        .map(NonEmptyList.fromFoldable(_))
        .unNone
        .map(insertProductItemsBatch(_))
        .evalMap(_.transact(xa))
        .compile
        .drain

  }

}
