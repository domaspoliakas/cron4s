import sbt.Keys.publishTo
import sbt.ThisBuild
import sbtghactions.GenerativePlugin.autoImport._
import sbtghpackages.GitHubPackagesKeys.{githubOwner, githubRepository}
import xerial.sbt.Sonatype.autoImport.{sonatypeProjectHosting, sonatypePublishToBundle}
import xerial.sbt.Sonatype.GitHubHosting

object GithubWorkflow {
  val SupportedScalaVersions = Seq("2.13.6", "2.12.14")
  val DefaultJVM             = "adopt@1.8"

  val JvmCond = s"matrix.platform == 'jvm'"

  ThisBuild / sonatypeProjectHosting := Some(
    GitHubHosting("precog", "cron4s", "bot@precog.com")
  )
  ThisBuild / publishTo := sonatypePublishToBundle.value
  ThisBuild / githubWorkflowPublishTargetBranches := Nil
  ThisBuild / githubWorkflowScalaVersions := SupportedScalaVersions
  ThisBuild / githubWorkflowJavaVersions := Seq("amazon-corretto@1.17")
  ThisBuild / githubOwner := "precog"
  ThisBuild / githubRepository := "cron4s"

  def settings =
    Seq(
      githubWorkflowTargetBranches := Seq("master"),
      githubWorkflowTargetTags ++= Seq("v*"),
      githubWorkflowPublish := Seq(
        WorkflowStep.Sbt(
          List("ci-release"),
          env = Map(
            "PGP_PASSPHRASE"    -> "${{ secrets.PGP_PASSPHRASE }}",
            "PGP_SECRET"        -> "${{ secrets.PGP_SECRET }}",
            "SONATYPE_PASSWORD" -> "${{ secrets.SONATYPE_PASSWORD }}",
            "SONATYPE_USERNAME" -> "${{ secrets.SONATYPE_USERNAME }}"
          )
        )
      ),
      githubWorkflowBuildMatrixAdditions +=
        "platform" -> List("jvm"),
      githubWorkflowBuildMatrixExclusions ++=
        githubWorkflowJavaVersions.value.filterNot(Set(DefaultJVM)).flatMap { java =>
          Seq(
            MatrixExclude(Map("java" -> java))
          )
        },
      githubWorkflowArtifactUpload := false,
      githubWorkflowBuild := Seq(
        WorkflowStep.Sbt(
          List("validateJVM", "validateBench"),
          name = Some("Validate JVM"),
          cond = Some(JvmCond)
        )
      )
    )

}
