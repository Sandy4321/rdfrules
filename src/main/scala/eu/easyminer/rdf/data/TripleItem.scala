package eu.easyminer.rdf.data

import org.apache.jena.datatypes.RDFDatatype
import org.apache.jena.datatypes.xsd.XSDDatatype
import org.apache.jena.graph.{Node, NodeFactory}

import scala.language.implicitConversions

/**
  * Created by Vaclav Zeman on 3. 10. 2017.
  */
trait TripleItem

object TripleItem {

  sealed trait Uri extends TripleItem

  case class LongUri(uri: String) extends Uri {
    override def toString: String = s"<$uri>"
  }

  case class PrefixedUri(prefix: String, nameSpace: String, localName: String) extends Uri {
    override def toString: String = prefix + ":" + localName
  }

  object PrefixedUri {
    def apply(longUri: LongUri): PrefixedUri = {
      val PrefixedUriPattern = "(.+)/(.+)".r
      longUri.uri match {
        case PrefixedUriPattern(nameSpace, localName) => PrefixedUri("", nameSpace, localName)
        case _ => throw new IllegalArgumentException
      }
    }
  }

  case class BlankNode(id: String) extends Uri {
    override def toString: String = "_:" + id
  }

  sealed trait Literal extends TripleItem

  case class Text(value: String) extends Literal {
    override def toString: String = "\"" + value + "\""
  }

  case class Number[T](value: T)(implicit val n: Numeric[T]) extends Literal {
    override def toString: String = value.toString
  }

  case class BooleanValue(value: Boolean) extends Literal {
    override def toString: String = if (value) "true" else "false"
  }

  implicit def numberToRdfDatatype(number: Number[_]): RDFDatatype = number match {
    case Number(_: Int) => XSDDatatype.XSDinteger
    case Number(_: Double) => XSDDatatype.XSDdouble
    case Number(_: Float) => XSDDatatype.XSDfloat
    case Number(_: Long) => XSDDatatype.XSDlong
    case Number(_: Short) => XSDDatatype.XSDshort
    case Number(_: Byte) => XSDDatatype.XSDbyte
    case _ => throw new IllegalArgumentException
  }

  implicit def tripleItemToJenaNode(tripleItem: TripleItem): Node = tripleItem match {
    case LongUri(uri) => NodeFactory.createURI(uri)
    case PrefixedUri(_, nameSpace, localName) => NodeFactory.createURI(nameSpace + localName)
    case BlankNode(id) => NodeFactory.createBlankNode(id)
    case Text(value) => NodeFactory.createLiteral(value)
    case number: Number[_] => NodeFactory.createLiteral(number.toString, number: RDFDatatype)
    case boolean: BooleanValue => NodeFactory.createLiteral(boolean.toString, XSDDatatype.XSDboolean)
  }

}