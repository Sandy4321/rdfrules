package eu.easyminer.rdf.rule

import eu.easyminer.rdf.data.TripleItem
import eu.easyminer.rdf.index.TripleItemHashIndex
import eu.easyminer.rdf.utils.TypedKeyMap.{Key, Value}

import scala.language.implicitConversions

/**
  * Created by Vaclav Zeman on 19. 6. 2017.
  */
sealed trait RuleConstraint extends Value {
  def companion: Key[RuleConstraint]
}

object RuleConstraint {

  case class OnlyPredicates(predicates: Set[Int]) extends RuleConstraint {
    def companion: OnlyPredicates.type = OnlyPredicates
  }

  implicit object OnlyPredicates extends Key[OnlyPredicates] {
    def apply(predicates: TripleItem.Uri*)(implicit mapper: TripleItemHashIndex): OnlyPredicates = new OnlyPredicates(predicates.iterator.map(mapper.getIndex(_)).toSet)
  }

  case class WithoutPredicates(predicates: Set[Int]) extends RuleConstraint {
    def companion: WithoutPredicates.type = WithoutPredicates
  }

  implicit object WithoutPredicates extends Key[WithoutPredicates] {
    def apply(predicates: TripleItem.Uri*)(implicit mapper: TripleItemHashIndex): WithoutPredicates = new WithoutPredicates(predicates.iterator.map(mapper.getIndex(_)).toSet)
  }

  case class WithInstances(onlyObjects: Boolean) extends RuleConstraint {
    def companion: WithInstances.type = WithInstances
  }

  implicit object WithInstances extends Key[WithInstances]

  case class WithoutDuplicitPredicates() extends RuleConstraint {
    def companion: WithoutDuplicitPredicates.type = WithoutDuplicitPredicates
  }

  implicit object WithoutDuplicitPredicates extends Key[WithoutDuplicitPredicates]

  implicit def ruleConstraintToKeyValue(ruleConstraint: RuleConstraint): (Key[RuleConstraint], RuleConstraint) = ruleConstraint.companion -> ruleConstraint

}