package com.github.propi.rdfrules.ruleset

import com.github.propi.rdfrules.data.TripleItem
import com.github.propi.rdfrules.index.TripleItemHashIndex
import com.github.propi.rdfrules.rule
import com.github.propi.rdfrules.rule.{Measure, Rule}
import com.github.propi.rdfrules.ruleset.ResolvedRule.Atom
import com.github.propi.rdfrules.utils.{Stringifier, TypedKeyMap}

import scala.language.implicitConversions

/**
  * Created by Vaclav Zeman on 17. 4. 2018.
  */
sealed trait ResolvedRule {
  val body: IndexedSeq[Atom]
  val head: Atom

  def ruleLength: Int = body.size + 1

  def measures: TypedKeyMap.Immutable[Measure]

  def withBody(body: IndexedSeq[Atom]): ResolvedRule

  def withHead(head: Atom): ResolvedRule

  def withMeasure(measure: Measure): ResolvedRule

  override def equals(obj: scala.Any): Boolean = obj match {
    case x: ResolvedRule => (this eq x) || (head == x.head && body == x.body)
    case _ => false
  }

  override def toString: String = Stringifier(this)
}

object ResolvedRule {

  case class Compressed private(body: IndexedSeq[Atom], head: Atom)(val rule: Rule.Simple) extends ResolvedRule {
    override def ruleLength: Int = rule.ruleLength

    def measures: TypedKeyMap.Immutable[Measure] = rule.measures

    def withBody(body: IndexedSeq[Atom]): ResolvedRule = this.copy(body = body)(rule)

    def withHead(head: Atom): ResolvedRule = this.copy(head = head)(rule)

    def withMeasure(measure: Measure): ResolvedRule = this.copy()(rule.copy()(measures + measure))
  }

  case class Simple private(body: IndexedSeq[Atom], head: Atom)(val measures: TypedKeyMap.Immutable[Measure]) extends ResolvedRule {
    def withBody(body: IndexedSeq[Atom]): ResolvedRule = this.copy(body = body)(measures)

    def withHead(head: Atom): ResolvedRule = this.copy(head = head)(measures)

    def withMeasure(measure: Measure): ResolvedRule = this.copy()(measures + measure)
  }

  sealed trait Atom {
    val subject: Atom.Item
    val predicate: TripleItem.Uri
    val `object`: Atom.Item

    override def equals(obj: scala.Any): Boolean = obj match {
      case x: Atom => (this eq x) || (subject == x.subject && predicate == x.predicate && `object` == x.`object`)
      case _ => false
    }
  }

  object Atom {

    sealed trait Item

    object Item {

      case class Variable private(value: String) extends Item

      case class Constant private(tripleItem: TripleItem) extends Item

      def apply(char: Char): Item = Variable("?" + char)

      def apply(variable: String): Item = Variable(variable)

      def apply(tripleItem: TripleItem): Item = Constant(tripleItem)

      implicit def apply(atomItem: rule.Atom.Item)(implicit mapper: TripleItemHashIndex): Item = atomItem match {
        case x: rule.Atom.Variable => apply(x.value)
        case rule.Atom.Constant(x) => apply(mapper.getTripleItem(x))
      }

    }

    case class Basic private(subject: Item, predicate: TripleItem.Uri, `object`: Item) extends Atom

    case class GraphBased private(subject: Item, predicate: TripleItem.Uri, `object`: Item)(val graphs: Set[TripleItem.Uri]) extends Atom

    def apply(subject: Item, predicate: TripleItem.Uri, `object`: Item): Atom = Basic(subject, predicate, `object`)

    implicit def apply(atom: rule.Atom)(implicit mapper: TripleItemHashIndex): Atom = atom match {
      case _: rule.Atom.Basic =>
        apply(atom.subject, mapper.getTripleItem(atom.predicate).asInstanceOf[TripleItem.Uri], atom.`object`)
      case x: rule.Atom.GraphBased =>
        GraphBased(atom.subject, mapper.getTripleItem(atom.predicate).asInstanceOf[TripleItem.Uri], atom.`object`)(x.graphsIterator.map(mapper.getTripleItem(_).asInstanceOf[TripleItem.Uri]).toSet)
    }

  }

  def apply(body: IndexedSeq[Atom], head: Atom, measures: Measure*): ResolvedRule = Simple(body, head)(TypedKeyMap(measures))

  implicit def apply(rule: Rule.Simple)(implicit mapper: TripleItemHashIndex): ResolvedRule = Compressed(
    rule.body.map(Atom.apply),
    rule.head
  )(rule)

  implicit val itemStringifier: Stringifier[ResolvedRule.Atom.Item] = {
    case ResolvedRule.Atom.Item.Variable(x) => x
    case ResolvedRule.Atom.Item.Constant(x) => x.toString
  }

  implicit val atomStringifier: Stringifier[ResolvedRule.Atom] = {
    case ResolvedRule.Atom.Basic(s, p, o) => s"(${Stringifier(s)} ${p.toString} ${Stringifier(o)})"
    case v@ResolvedRule.Atom.GraphBased(s, p, o) =>
      def bracketGraphs(strGraphs: String): String = if (v.graphs.size == 1) strGraphs else s"[$strGraphs]"

      s"(${Stringifier(s)} ${p.toString} ${Stringifier(o)} ${bracketGraphs(v.graphs.iterator.map(_.toString).mkString(", "))})"
  }

  implicit val resolvedRuleStringifier: Stringifier[ResolvedRule] = (v: ResolvedRule) => v.body.map(x => Stringifier(x)).mkString(" ^ ") +
    " -> " +
    Stringifier(v.head) + " | " +
    v.measures.iterator.toList.sortBy(_.companion).iterator.map(x => Stringifier(x)).mkString(", ")

  implicit val resolvedRuleOrdering: Ordering[ResolvedRule] = Ordering.by[ResolvedRule, TypedKeyMap.Immutable[Measure]](_.measures)

}