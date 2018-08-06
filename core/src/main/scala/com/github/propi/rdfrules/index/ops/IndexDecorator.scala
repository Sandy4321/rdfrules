package com.github.propi.rdfrules.index.ops

import java.io.{File, OutputStream}

import com.github.propi.rdfrules.data.Dataset
import com.github.propi.rdfrules.index.{Index, TripleHashIndex, TripleItemHashIndex}

/**
  * Created by Vaclav Zeman on 19. 7. 2018.
  */
class IndexDecorator(index: Index) extends Index {

  def tripleMap[T](f: TripleHashIndex => T): T = index.tripleMap(f)

  def tripleItemMap[T](f: TripleItemHashIndex => T): T = index.tripleItemMap(f)

  def toDataset: Dataset = index.toDataset

  def cache(os: => OutputStream): Unit = index.cache(os)

  def cache(file: File): Unit = index.cache(file)

  def cache(file: String): Unit = index.cache(file)

  def newIndex: Index = index.newIndex

  def withEvaluatedLazyVals: Index = index.withEvaluatedLazyVals

}