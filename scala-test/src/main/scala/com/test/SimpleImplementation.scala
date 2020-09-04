package com.test

class SimpleImplementation extends SimpleTrait[SimpleImplementation] {
  override def doSomething(): Unit = println(getClass.getSimpleName)
  override def isInstanceOf(instance: SimpleImplementation): Boolean = this.isInstanceOf(instance)
}
