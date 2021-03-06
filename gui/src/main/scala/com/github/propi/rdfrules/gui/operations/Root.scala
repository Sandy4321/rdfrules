package com.github.propi.rdfrules.gui.operations

import com.github.propi.rdfrules.gui.{Operation, OperationInfo, Property}
import com.thoughtworks.binding.Binding.{Constants, Var}

/**
  * Created by Vaclav Zeman on 21. 7. 2018.
  */
class Root extends Operation {
  val info: OperationInfo = OperationInfo.Root
  val properties: Constants[Property] = Constants()
  val previousOperation: Var[Option[Operation]] = Var(None)
}