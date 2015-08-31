lazy val root = (project in file(".")).
  settings(
    organization := "org.yamltables",
    name := "MdYamlTables",
    version := "0.1alpha",
    scalaVersion := "2.11.6",
    libraryDependencies ++= Seq(
      "org.yaml" % "snakeyaml" % "1.16",
      "org.pegdown" % "pegdown" % "1.5.0",
      "org.scalatest" % "scalatest_2.11" % "2.2.5"
    )
  )