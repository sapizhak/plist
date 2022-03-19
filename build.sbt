ThisBuild / scalaVersion := "2.13.8"

lazy val root =
  (project in file("."))
    .settings(
      name := "plist",
      libraryDependencies ++=
        Seq(
          "org.typelevel"         %% "cats-effect"               % "3.3.7",
          "org.typelevel"         %% "cats-core"                 % "2.7.0",
          "com.github.pureconfig" %% "pureconfig-core"           % "0.17.1",
          "com.github.pureconfig" %% "pureconfig-generic"        % "0.17.1",
          "org.http4s"            %% "http4s-core"               % "0.23.10",
          "org.http4s"            %% "http4s-dsl"                % "0.23.10",
          "org.http4s"            %% "http4s-prometheus-metrics" % "0.23.10",
          "org.http4s"            %% "http4s-ember-core"         % "0.23.10",
          "org.http4s"            %% "http4s-ember-server"       % "0.23.10",
          "org.http4s"            %% "http4s-circe"              % "0.23.10",
          "io.circe"              %% "circe-refined"             % "0.14.1",
          "eu.timepit"            %% "refined"                   % "0.9.28",
          "eu.timepit"            %% "refined-pureconfig"        % "0.9.28",
          "org.typelevel"         %% "log4cats-core"             % "2.2.0",
          "org.typelevel"         %% "log4cats-slf4j"            % "2.2.0",
          "org.tpolecat"          %% "doobie-core"               % "1.0.0-RC2",
          "org.tpolecat"          %% "doobie-postgres"           % "1.0.0-RC2",
          "org.tpolecat"          %% "doobie-refined"            % "1.0.0-RC2",
          "org.tpolecat"          %% "doobie-hikari"             % "1.0.0-RC2",
          "com.nrinaudo"          %% "kantan.csv"                % "0.6.2",
          "com.nrinaudo"          %% "kantan.csv-refined"        % "0.6.2",
          "com.nrinaudo"          %% "kantan.csv-generic"        % "0.6.2",
          "ch.qos.logback"         % "logback-classic"           % "1.2.11" % Runtime,
          "io.prometheus"          % "simpleclient"              % "0.15.0",
          "org.flywaydb"           % "flyway-core"               % "8.5.2"
        )
    )
