package com.github.propi.rdfrules.http.task

import com.github.propi.rdfrules.data.Dataset

/**
  * Created by Vaclav Zeman on 7. 8. 2018.
  */
trait Task[I, O] {
  val companion: TaskDefinition

  def execute(input: I): O

  def name: String = companion.name

  private def buildNewTask[T](taskName: String, f: I => T) = {
    val rootName = name
    new Task[I, T] {
      val companion: TaskDefinition = new TaskDefinition {
        val name: String = s"$rootName -> $taskName"
      }

      def execute(input: I): T = f(input)
    }
  }

  final def andThen[T](task: Task[O, T]): Task[I, T] = buildNewTask(task.name, (this.execute _).andThen(task.execute))

  final def andThenAny[T](task: Task[Any, T]): Task[I, T] = buildNewTask(task.name, (this.execute _).andThen(task.execute))
}

object Task {

  object NoInput

  class MergeDatasets private(datasets: List[Task[Task.NoInput.type, Dataset]]) extends Task[NoInput.type, Dataset] {
    def this() = this(Nil)

    val companion: TaskDefinition = MergeDatasets

    def addDatasetTasks(datasets: List[Task[Task.NoInput.type, Dataset]]) = new MergeDatasets(datasets)

    def execute(input: NoInput.type): Dataset = datasets.iterator.map(_.execute(NoInput)).foldLeft(Dataset())(_ + _)
  }

  object MergeDatasets extends TaskDefinition {
    val name: String = "MergeDatasets"
  }

}