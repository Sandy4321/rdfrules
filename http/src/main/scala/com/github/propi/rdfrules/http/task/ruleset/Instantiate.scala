package com.github.propi.rdfrules.http.task.ruleset

import com.github.propi.rdfrules.http.task.{Task, TaskDefinition}
import com.github.propi.rdfrules.ruleset.{CoveredPaths, ResolvedRule, Ruleset}

/**
  * Created by Vaclav Zeman on 10. 8. 2018.
  */
class Instantiate(rule: ResolvedRule, part: CoveredPaths.Part) extends Task[Ruleset, Ruleset] {
  val companion: TaskDefinition = Instantiate

  def execute(input: Ruleset): Ruleset = {
    val ruleParts = rule.body.toSet -> rule.head
    input.filterResolved { x =>
      val ruleParts2 = x.body.toSet -> rule.head
      ruleParts2 == ruleParts
    }.coveredPaths(part).headOption.map(_.paths).getOrElse(Ruleset(input.index, Nil, true))
  }
}

object Instantiate extends TaskDefinition {
  val name: String = "Instantiate"
}

