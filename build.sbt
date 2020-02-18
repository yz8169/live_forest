name := "live_forest"
 
version := "1.0" 
      
lazy val `live_forest` = (project in file(".")).enablePlugins(PlayScala)


resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

resolvers += "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/"
      
scalaVersion := "2.12.2"

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice )

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

libraryDependencies += "com.typesafe.play" %% "play-slick" % "3.0.1"

libraryDependencies += "com.typesafe.slick" %% "slick-codegen" % "3.2.1"

libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.25"

libraryDependencies += "commons-io" % "commons-io" % "2.5"

libraryDependencies += "com.github.tototoshi" %% "slick-joda-mapper" % "2.3.0"

libraryDependencies += "com.github.jurajburian" %% "mailer" % "1.2.2"

libraryDependencies += "com.typesafe.play" %% "play-joda-forms" % "2.6.15"

libraryDependencies += "org.jxls" % "jxls" % "2.4.7"

libraryDependencies += "com.itextpdf" % "itext7-core" % "7.1.4"

libraryDependencies += "io.github.cloudify" %% "spdf" % "1.4.0"

libraryDependencies += "org.apache.poi" % "poi-ooxml" % "3.15"








      