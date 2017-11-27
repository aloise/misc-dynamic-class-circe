name := "ModifiableRecordTest"

val circeVersion = "0.8.0"
val refinedVersion = "0.8.4"
//val extruderVersion = "0.6.3"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "io.circe" %% "circe-refined" % circeVersion,
  "com.chuusai" %% "shapeless" % "2.3.2",
  "eu.timepit" %% "refined" % refinedVersion,
  "eu.timepit" %% "refined-cats" % refinedVersion, // optional
  "eu.timepit" %% "refined-eval" % refinedVersion,
  "eu.timepit" %% "singleton-ops" % "0.2.1"
//  "extruder" %% "extruder" % extruderVersion,
//  "extruder" %% "extruder-refined" % extruderVersion

)