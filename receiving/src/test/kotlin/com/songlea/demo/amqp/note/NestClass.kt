package com.songlea.demo.amqp.note

/**
 * 嵌套类
 */
// 外部类
class NestClass {
    private val bar: Int = 1

    // 嵌套类
    class Nested {
        fun foo() = 2
    }

    // 内部类
    inner class Inner {
        fun innerTest() = this@NestClass.bar
    }
}



fun main() {
    // 调用格式：外部类.嵌套类.嵌套类方法/属性
    println(NestClass.Nested().foo())

    // 内部类可以引用外部类的成员，例如：成员属性
    println(NestClass().Inner().innerTest())
}