package mimabuild

import sbt._
import sbt.librarymanagement.{ SemanticSelector, VersionNumber }
import sbt.Classpaths.pluginProjectID
import sbt.Keys._
import com.typesafe.tools.mima.core._
import com.typesafe.tools.mima.core.ProblemFilters.exclude
import com.typesafe.tools.mima.plugin.MimaPlugin.autoImport._

object MimaSettings {
  // clear out mimaBinaryIssueFilters when changing this
  val mimaPreviousVersion = "0.6.1"

  val mimaSettings = Def.settings (
    mimaPreviousArtifacts := Set(pluginProjectID.value.withRevision(mimaPreviousVersion)
      .withExplicitArtifacts(Vector()) // defaultProjectID uses artifacts.value which breaks it =/
    ),
    mimaBinaryIssueFilters ++= Seq(
      // The main public API is:
      // * com.typesafe.tools.mima.plugin.MimaPlugin
      // * com.typesafe.tools.mima.plugin.MimaKeys
      // * com.typesafe.tools.mima.core.ProblemFilters
      // * com.typesafe.tools.mima.core.*Problem
      // to a less degree (some re-implementors):
      // * com.typesafe.tools.mima.lib.MiMaLib.collectProblems

      // Dropped some useless/dead methods in Config & the whole Settings subclass
      ProblemFilters.exclude[MemberProblem]("com.typesafe.tools.mima.core.Config.*"),
      ProblemFilters.exclude[MissingClassProblem]("com.typesafe.tools.mima.core.Settings"),

      // In-lined an internal package method
      ProblemFilters.exclude[DirectMissingMethodProblem]("com.typesafe.tools.mima.core.package.asClassPathString"),
      // Dropped exposing MiMaLib's log
      ProblemFilters.exclude[DirectMissingMethodProblem]("com.typesafe.tools.mima.lib.MiMaLib.log"),
      // Made MiMaLib final
      ProblemFilters.exclude[FinalClassProblem]("com.typesafe.tools.mima.lib.MiMaLib"),
      // Made collectProblems take file params rather than strings
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("com.typesafe.tools.mima.lib.MiMaLib.collectProblems"),

      // Inlined or replaced
      ProblemFilters.exclude[DirectMissingMethodProblem]("com.typesafe.tools.mima.core.package.resolveClassPath"),
      ProblemFilters.exclude[DirectMissingMethodProblem]("com.typesafe.tools.mima.core.package.dirClassPath"),
      ProblemFilters.exclude[DirectMissingMethodProblem]("com.typesafe.tools.mima.core.package.baseClassPath"),
      ProblemFilters.exclude[DirectMissingMethodProblem]("com.typesafe.tools.mima.core.package.reporterClassPath"),
      ProblemFilters.exclude[DirectMissingMethodProblem]("com.typesafe.tools.mima.core.package.classFilesFrom"),
      ProblemFilters.exclude[DirectMissingMethodProblem]("com.typesafe.tools.mima.core.package.definitionsPackageInfo"),
      ProblemFilters.exclude[DirectMissingMethodProblem]("com.typesafe.tools.mima.core.package.definitionsTargetPackages"),
      ProblemFilters.exclude[DirectMissingMethodProblem]("com.typesafe.tools.mima.core.package.packagesFrom"),

      // Redefined (or dropped) internal methods and classes
      ProblemFilters.exclude[DirectMissingMethodProblem]("com.typesafe.tools.mima.core.ProblemReporting.isReported"),
      ProblemFilters.exclude[DirectMissingMethodProblem]("com.typesafe.tools.mima.plugin.SbtMima.loadMimaIgnoredProblems"),
      ProblemFilters.exclude[MissingClassProblem]("com.typesafe.tools.mima.plugin.SbtMima$ParsingException*"),

      // defined final
      ProblemFilters.exclude[FinalClassProblem]("com.typesafe.tools.mima.core.Definitions"),

      // widened param/result types
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("com.typesafe.tools.mima.core.Definitions.root"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("com.typesafe.tools.mima.core.DefinitionsTargetPackageInfo.this"),
    ),
  )
}
