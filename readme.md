# plist

tech-radar: scala, scalafmt, sbt, tagless final, kantan.csv, http4s, fs2, doobie, refined, pureconfig, cats, cats-effect

minimalistic http server app that consumes and produces csv documents upon request, csv rows are persisted in a postgres database. 

csv format:
- separator: pipe (|)
- line separator: LF (\n)
- header: (produktId: NonEmptyString, name: NonEmptyString, beschreibung: Option[NonEmptyString], preis: PosFloat, summeBestand: NonNegInt)

http endpoints: 
- `PUT /articles/:{lines} -H 'Content-Type: text/csv'` (consumes csv) 
- `GET /articles/:{lines}` (produces csv, returning cheapest article of a product, its total stock and other data)

