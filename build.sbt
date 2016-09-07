lazy val commonSettings = Seq(
  organization := "org.opensirf",
  version := "1.0.0"
)

lazy val root = (project in file(".")).
  enablePlugins(WarPlugin).
  settings(commonSettings: _*).
  settings(
    name := "OpenSIRF JAX-RS",
        artifactName := { (sv: ScalaVersion, module: ModuleID, artifact: Artifact) =>
        artifact.name + "-" + version.value + "." + artifact.extension
        },
    version := "1.0.0",
    crossTarget := new java.io.File("target"),
    webappWebInfClasses := true,
    packageOptions in sbt.Keys.`package` ++=
        (packageOptions in (Compile, packageBin)).value filter {
        case x: Package.ManifestAttributes => true
        case x => false
    }
)

crossPaths := false
publishTo := Some(Resolver.url("Artifactory Realm", new URL("http://200.144.189.109:58082/artifactory"))(Resolver.ivyStylePatterns))
credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")
publishMavenStyle := false
isSnapshot := true

publishArtifact in (Compile, packageDoc) := false
publishArtifact in (Compile, packageSrc) := false

libraryDependencies += "org.opensirf.core" % "opensirf-core" % "1.0.0"

resolvers += Resolver.url("SIRF Artifactory", url("http://200.144.189.109:58082/artifactory"))(Resolver.ivyStylePatterns)

