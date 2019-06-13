package com.songlea.demo.amqp.note

/**
 * 匿名内部类
 */
class AnonymousInnerClass {
    var v = "成员属性"

    fun setInterFace(test: TestInterFace) {
        test.test()
    }
}

interface TestInterFace {
    fun test()
}

fun main() {
    val test = AnonymousInnerClass()

    test.setInterFace(object : TestInterFace {
        override fun test() {
            print("对象表达式创建匿名内部类的实例,v:${test.v}")
        }
    })
}