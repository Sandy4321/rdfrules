package com.github.propi.rdfrules.http.task.ruleset

import com.github.propi.rdfrules.http.task.{Task, TaskDefinition}
import com.github.propi.rdfrules.ruleset.Ruleset

/**
  * Created by Vaclav Zeman on 9. 8. 2018.
  */
class Drop(n: Int) extends Task[Ruleset, Ruleset] {
  val companion: TaskDefinition = Drop

  def execute(input: Ruleset): Ruleset = input.drop(n)
}

object Drop extends TaskDefinition {
  val name: String = "DropRules"
}