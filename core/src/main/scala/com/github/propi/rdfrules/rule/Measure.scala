package com.github.propi.rdfrules.rule

import com.github.propi.rdfrules.utils.{Stringifier, TypedKeyMap}
import com.github.propi.rdfrules.utils.TypedKeyMap.{Key, Value}

import scala.language.implicitConversions

/**
  * Created by Vaclav Zeman on 16. 6. 2017.
  */
sealed trait Measure extends Value {
  def companion: Key[Measure]
}

object Measure {

  case class Support(value: Int) extends Measure {
    def companion: Support.type = Support
  }

  implicit object Support extends Key[Support]

  case class HeadCoverage(value: Double) extends Measure {
    def companion: HeadCoverage.type = HeadCoverage
  }

  implicit object HeadCoverage extends Key[HeadCoverage]

  case class HeadSize(value: Int) extends Measure {
    def companion: HeadSize.type = HeadSize
  }

  implicit object HeadSize extends Key[HeadSize]

  case class BodySize(value: Int) extends Measure {
    def companion: BodySize.type = BodySize
  }

  implicit object BodySize extends Key[BodySize]

  case class Confidence(value: Double) extends Measure {
    def companion: Confidence.type = Confidence
  }

  implicit object Confidence extends Key[Confidence]

  case class HeadConfidence(value: Double) extends Measure {
    def companion: HeadConfidence.type = HeadConfidence
  }

  implicit object HeadConfidence extends Key[HeadConfidence]

  case class Lift(value: Double) extends Measure {
    def companion: Lift.type = Lift
  }

  implicit object Lift extends Key[Lift]

  case class PcaBodySize(value: Int) extends Measure {
    def companion: PcaBodySize.type = PcaBodySize
  }

  implicit object PcaBodySize extends Key[PcaBodySize]

  case class PcaConfidence(value: Double) extends Measure {
    def companion: PcaConfidence.type = PcaConfidence
  }

  implicit object PcaConfidence extends Key[PcaConfidence]

  case class Cluster(number: Int) extends Measure {
    def companion: Cluster.type = Cluster
  }

  implicit object Cluster extends Key[Cluster]

  def unapply(arg: Measure): Option[Double] = arg match {
    case Measure.BodySize(x) => Some(x)
    case Measure.Confidence(x) => Some(x)
    case Measure.HeadConfidence(x) => Some(x)
    case Measure.HeadCoverage(x) => Some(x)
    case Measure.HeadSize(x) => Some(x)
    case Measure.Lift(x) => Some(x)
    case Measure.PcaBodySize(x) => Some(x)
    case Measure.PcaConfidence(x) => Some(x)
    case Measure.Support(x) => Some(x)
    case Measure.Cluster(x) => Some(x)
  }

  implicit def mesureToKeyValue(measure: Measure): (Key[Measure], Measure) = measure.companion -> measure

  implicit val measureKeyOrdering: Ordering[Key[Measure]] = {
    val map = Iterator[Key[Measure]](Support, HeadCoverage, Confidence, PcaConfidence, Lift, HeadConfidence, HeadSize, BodySize, PcaBodySize, Cluster).zipWithIndex.map(x => x._1 -> (x._2 + 1)).toMap
    Ordering.by[Key[Measure], Int](map.getOrElse(_, 0))
  }

  implicit val measureOrdering: Ordering[Measure] = Ordering.by[Measure, Double] {
    case Measure.BodySize(x) => x
    case Measure.Confidence(x) => x
    case Measure.HeadConfidence(x) => x
    case Measure.HeadCoverage(x) => x
    case Measure.HeadSize(x) => x
    case Measure.Lift(x) => x
    case Measure.PcaBodySize(x) => x
    case Measure.PcaConfidence(x) => x
    case Measure.Support(x) => x
    case Measure.Cluster(x) => x * -1
  }.reverse

  implicit val measuresOrdering: Ordering[TypedKeyMap.Immutable[Measure]] = Ordering.by[TypedKeyMap.Immutable[Measure], (Measure, Measure, Measure, Measure, Measure)] { measures =>
    (
      measures.get(Measure.Cluster).getOrElse(Measure.Cluster(0)),
      measures.get(Measure.PcaConfidence).getOrElse(Measure.PcaConfidence(0)),
      measures.get(Measure.Lift).getOrElse(Measure.Lift(0)),
      measures.get(Measure.Confidence).getOrElse(Measure.Confidence(0)),
      measures.get(Measure.HeadCoverage).getOrElse(Measure.HeadCoverage(0))
    )
  }

  implicit val measureStringifier: Stringifier[Measure] = {
    case Measure.Support(v) => s"support: $v"
    case Measure.HeadCoverage(v) => s"headCoverage: $v"
    case Measure.Confidence(v) => s"confidence: $v"
    case Measure.Lift(v) => s"lift: $v"
    case Measure.PcaConfidence(v) => s"pcaConfidence: $v"
    case Measure.HeadConfidence(v) => s"headConfidence: $v"
    case Measure.HeadSize(v) => s"headSize: $v"
    case Measure.BodySize(v) => s"bodySize: $v"
    case Measure.PcaBodySize(v) => s"pcaBodySize: $v"
    case Measure.Cluster(v) => s"cluster: $v"
  }

}