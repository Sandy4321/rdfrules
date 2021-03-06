package com.github.propi.rdfrules.http.task.model

import com.github.propi.rdfrules.data.TripleItem
import com.github.propi.rdfrules.http.task.{Task, TaskDefinition, TripleItemMatcher}
import com.github.propi.rdfrules.model.Model
import com.github.propi.rdfrules.rule.{Measure, RulePattern}
import com.github.propi.rdfrules.utils.TypedKeyMap

/**
  * Created by Vaclav Zeman on 7. 8. 2018.
  */
class FilterRules(measures: Seq[(Option[TypedKeyMap.Key[Measure]], TripleItemMatcher.Number)],
                  patterns: Seq[RulePattern]) extends Task[Model, Model] {
  val companion: TaskDefinition = FilterRules

  def execute(input: Model): Model = {
    val rulesetFiltered = if (measures.nonEmpty) {
      input.filter(rule => measures.forall { case (measure, matcher) =>
        measure match {
          case Some(measure) => rule.measures.get(measure).collect {
            case Measure(x) => TripleItem.Number(x)
          }.exists(matcher.matchAll(_).nonEmpty)
          case None => matcher.matchAll(TripleItem.Number(rule.ruleLength)).nonEmpty
        }
      })
    } else {
      input
    }
    patterns match {
      case Seq(head, tail@_*) => rulesetFiltered.filter(head, tail: _*)
      case _ => rulesetFiltered
    }
  }
}

object FilterRules extends TaskDefinition {
  val name: String = "FilterRules"
}