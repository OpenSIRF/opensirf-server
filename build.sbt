val baseVersion = SettingKey[String]("base-version", "Base version of the project without timestamp.")

lazy val commonSettings = Seq(
  organization := "org.opensirf"
)

lazy val root = (project in file(".")).
  enablePlugins(WarPlugin).
  settings(commonSettings: _*).
  settings(
    name := "OpenSIRF JAX-RS",
	artifactName := { (sv: ScalaVersion, module: ModuleID, artifact: Artifact) =>
        artifact.name + "-" + baseVersion.value + "." + artifact.extension    
	},
    baseVersion := "1.0.0",
    crossTarget := new java.io.File("target"),
    version := new String(baseVersion.value) + "-" + new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date()).toString(),
    webappWebInfClasses := true,
    packageOptions in sbt.Keys.`package` += Package.ManifestAttributes( java.util.jar.Attributes.Name.SEALED -> "true" )
)
