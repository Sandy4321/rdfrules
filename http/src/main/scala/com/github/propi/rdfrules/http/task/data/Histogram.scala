package com.github.propi.rdfrules.http.task.data

import com.github.propi.rdfrules.data
import com.github.propi.rdfrules.data.Dataset
import com.github.propi.rdfrules.http.task.{Task, TaskDefinition}

/**
  * Created by Vaclav Zeman on 9. 8. 2018.
  */
class Histogram(s: Boolean, p: Boolean, o: Boolean) extends Task[Dataset, collection.Map[data.Histogram.Key, Int]] {
  val companion: TaskDefinition = Histogram

  def execute(input: Dataset): collection.Map[data.Histogram.Key, Int] = input.histogram(s, p, o)
}

object Histogram extends TaskDefinition {
  val name: String = "Histogram"
}