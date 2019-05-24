package com.songlea.demo.amqp.coroutines

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

typealias aliasMap = Map<String, Any>

fun main() = runBlocking {
    launch {
        delay(1000L)
        println("World!")
    }
    println("Hello,")

    var a = 1
    var b = 2
    a = b.also { b = a }
    println("a = $a, b = $b")

   val test: aliasMap = mapOf<String, Any>("songlea" to 666)
    println(test)
}