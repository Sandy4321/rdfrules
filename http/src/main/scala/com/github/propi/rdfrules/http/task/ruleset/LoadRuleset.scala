package com.github.propi.rdfrules.http.task.ruleset

import com.github.propi.rdfrules.http.Workspace
import com.github.propi.rdfrules.http.task.{Task, TaskDefinition}
import com.github.propi.rdfrules.index.Index
import com.github.propi.rdfrules.model.Model
import com.github.propi.rdfrules.ruleset.{Ruleset, RulesetSource}
import com.github.propi.rdfrules.utils.Debugger

/**
  * Created by Vaclav Zeman on 7. 8. 2018.
  */
class LoadRuleset(path: String, format: Option[Either[Boolean, RulesetSource]])(implicit debugger: Debugger) extends Task[Index, Ruleset] {
  val companion: TaskDefinition = LoadRuleset

  def execute(input: Index): Ruleset = {
    val ruleset = format match {
      case Some(Right(source)) => Ruleset(input, Workspace.path(path))(source)
      case Some(Left(false)) => Model.fromCache(Workspace.path(path)).toRuleset(input)
      case Some(Left(true)) => Ruleset.fromCache(input, Workspace.path(path))
      case None => Ruleset.fromCache(input, Workspace.path(path))
    }
    ruleset.withDebugger
  }
}

object LoadRuleset extends TaskDefinition {
  val name: String = "LoadRuleset"
}