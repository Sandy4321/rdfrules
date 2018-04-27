package com.github.propi.rdfrules.ruleset.formats

import java.io.{OutputStreamWriter, PrintWriter}

import com.github.propi.rdfrules.data.TripleItem
import com.github.propi.rdfrules.rule.Measure
import com.github.propi.rdfrules.ruleset.ResolvedRule.Atom
import com.github.propi.rdfrules.ruleset.{ResolvedRule, RulesetSource, RulesetWriter}
import com.github.propi.rdfrules.utils.OutputStreamBuilder
import spray.json._

/**
  * Created by Vaclav Zeman on 18. 4. 2018.
  */
object Json {

  implicit val tripleItemJsonWriter: RootJsonWriter[TripleItem] = {
    case x: TripleItem.Uri => JsString(x.toString)
    case TripleItem.Text(x) => JsString(x)
    case TripleItem.NumberDouble(x) => JsNumber(x)
    case TripleItem.BooleanValue(x) => JsBoolean(x)
    case x: TripleItem => JsString(x.toString)
  }

  implicit val mappedAtomItemJsonWriter: RootJsonWriter[Atom.Item] = {
    case Atom.Item.Variable(x) => JsObject("type" -> JsString("variable"), "value" -> JsString(x.toString()))
    case Atom.Item.Constant(x) => JsObject("type" -> JsString("constant"), "value" -> x.toJson)
  }

  implicit val mappedAtomJsonWriter: RootJsonWriter[Atom] = (obj: Atom) => JsObject(
    "subject" -> obj.subject.toJson,
    "predicate" -> obj.predicate.asInstanceOf[TripleItem].toJson,
    "object" -> obj.`object`.toJson
  )

  implicit val measureJsonWriter: RootJsonWriter[Measure] = {
    case Measure.BodySize(x) => JsObject("name" -> JsString("bodySize"), "value" -> JsNumber(x))
    case Measure.Confidence(x) => JsObject("name" -> JsString("confidence"), "value" -> JsNumber(x))
    case Measure.HeadConfidence(x) => JsObject("name" -> JsString("headConfidence"), "value" -> JsNumber(x))
    case Measure.HeadCoverage(x) => JsObject("name" -> JsString("headCoverage"), "value" -> JsNumber(x))
    case Measure.HeadSize(x) => JsObject("name" -> JsString("headSize"), "value" -> JsNumber(x))
    case Measure.Lift(x) => JsObject("name" -> JsString("lift"), "value" -> JsNumber(x))
    case Measure.PcaBodySize(x) => JsObject("name" -> JsString("pcaBodySize"), "value" -> JsNumber(x))
    case Measure.PcaConfidence(x) => JsObject("name" -> JsString("pcaConfidence"), "value" -> JsNumber(x))
    case Measure.PcaLift(x) => JsObject("name" -> JsString("pcaLift"), "value" -> JsNumber(x))
    case Measure.Support(x) => JsObject("name" -> JsString("support"), "value" -> JsNumber(x))
    case Measure.Cluster(x) => JsObject("name" -> JsString("cluster"), "value" -> JsNumber(x))
  }

  implicit val resolvedRuleJsonWriter: RootJsonWriter[ResolvedRule] = (obj: ResolvedRule) => JsObject(
    "head" -> obj.head.toJson,
    "body" -> JsArray(obj.body.iterator.map(_.toJson).toVector),
    "measures" -> JsArray(obj.measures.iterator.map(_.toJson).toVector)
  )

  implicit val jsonRulesetWriter: RulesetWriter[RulesetSource.Json.type] = (rules: Traversable[ResolvedRule], outputStreamBuilder: OutputStreamBuilder) => {
    val writer = new PrintWriter(new OutputStreamWriter(outputStreamBuilder.build, "UTF-8"))
    try {
      writer.println('[')
      for (rule <- rules) {
        writer.println(rule.toJson.prettyPrint)
      }
      writer.println(']')
    } finally {
      writer.close()
    }
  }

}