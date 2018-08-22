name := "ModifiableRecordTest"

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.7")

val circeVersion = "0.9.3"
val refinedVersion = "0.9.2"

scalacOptions ++= Seq(
  "-Xlog-implicits"
)

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "io.circe" %% "circe-refined" % circeVersion,
  "com.chuusai" %% "shapeless" % "2.3.2",
  "eu.timepit" %% "refined" % refinedVersion,
  "eu.timepit" %% "refined-cats" % refinedVersion, // optional
  "eu.timepit" %% "refined-eval" % refinedVersion,
  "eu.timepit" %% "singleton-ops" % "0.2.1",
  "com.slamdata" %% "matryoshka-core" % "0.21.3"
//  "extruder" %% "extruder" % extruderVersion,
//  "extruder" %% "extruder-refined" % extruderVersion

)