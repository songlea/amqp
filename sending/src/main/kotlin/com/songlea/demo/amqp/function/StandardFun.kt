package com.songlea.demo.amqp.function

import java.io.File

// 通用的扩展标准函数run,with,let,also,apply,takeUnless,takeIf和repeat(Standard.kt文件中)
fun main(args: Array<String>) {
    testScope()
    testRun()
    testApply()
    testLet()
    testAlso()
    testWith()
    testTaskIf()
    testTaskUnless()
    testRepeat()
    makeDir("B:\\abc\\test")
}

@Suppress("NAME_SHADOWING")
fun testScope() {
    // 作用域：通过一段代码来看一下run函数的作用域，对于其它函数来说当然也是类似的
    val animal = "cat"
    run {
        val animal = "dog"
        println(animal) // dog
    }
    println(animal) //cat
}

fun testRun() {
    // 调用run函数块。返回值为函数块最后一行，或者指定return表达式
    // 函数定义：public inline fun <R> run(block: () -> R): R
    val a = run {
        println("run exec")
        return@run 3
    }
    println(a)

    // 调用某对象的run函数，在函数块内可以通过 this 指代该对象，返回值为函数块的最后一行或指定return表达式
    // 函数定义：public inline fun <T, R> T.run(block: T.() -> R): R
    val b = "string".run {
        println(this)
        3
    }
    println(b)
}

fun testApply() {
    // 调用某对象的apply函数，在函数块内可以通过 this 指代该对象，返回值为该对象自己
    // 函数定义：public inline fun <T> T.apply(block: T.() -> Unit): T
    val a = "string".apply {
        println(this)
    }
    println(a)
}

fun testLet() {
    // 调用某对象的let函数，则该对象为函数的参数，在函数块内可以通过 it 指代该对象。返回值为函数块的最后一行或指定return表达式
    // 函数定义：public inline fun <T, R> T.let(block: (T) -> R): R
    val original = "abc"
    original.let {
        println("The original String is $it") // "abc"
        it.reversed().repeat(1).takeIf { it.contains("c", true) }
    }.let {
        println("The reverse String is $it") // "cba"
        it?.length ?: 0
    }.let {
        println("The length of the String is $it") // 3
    }
}

fun testAlso() {
    val original = "abc"
    // 调用某对象的also函数，则该对象为函数的参数，在函数块内可以通过 it 指代该对象。返回值为该对象自己
    // 不管调用多少次返回的都是原来的original对象
    // 函数定义：public inline fun <T> T.also(block: (T) -> Unit): T
    original.also {
        println("The original String is $it") // "abc"
        it.reversed()
    }.also {
        println("The reverse String is $it") // "abc"
        it.length
    }.also {
        println("The length of the String is $it") // "abc"
    }
}

fun testWith() {
    // with函数和前面的几个函数使用方式略有不同，因为它不是以扩展的形式存在的，它是将某对象作为函数的参数，
    // 在函数块内可以通过 this 指代该对象，返回值为函数块的最后一行或指定return表达式
    // 函数定义：public inline fun <T, R> with(receiver: T, block: T.() -> R): R
    val a = with("string") {
        println(this)
        3
    }
    println(a)
}

fun testTaskIf() {
    // taskIf函数如果表达式为true时返回当前对象this，否则返回null
    // 函数定义：public inline fun <T> T.takeIf(predicate: (T) -> Boolean): T?
    val a = "String".takeIf {
        it.contains("t") // 包含t返回true
    }
    println(a) // "String"
}

fun testTaskUnless() {
    // 与taskIf作用相反
    // 函数定义：public inline fun <T> T.takeUnless(predicate: (T) -> Boolean): T?
    val a = "String".takeUnless {
        it.contains("a") // 不包含t返回false
    }
    println(a) // "String"
}

fun testRepeat() {
    // 重复执行action times次,action每次传入的参数为times的遍历值
    // 函数定义：public inline fun repeat(times: Int, action: (Int) -> Unit)
    repeat(4, {
        println("time $it")
    })
}

fun makeDir(path: String) = path.let { File(it) }.also { it.mkdirs() }

