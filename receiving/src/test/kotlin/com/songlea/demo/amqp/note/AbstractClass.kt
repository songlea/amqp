package com.songlea.demo.amqp.note

/**
 * 抽象类
 * 注意：无需对抽象类或抽象成员标注open注解
 */
abstract class AbstractClass : Base() {

    // 抽象成员在类中不存在具体的实现
    abstract override fun f()

}

open class Base {

    open fun f() {}

}