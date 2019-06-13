package com.songlea.demo.amqp.note

fun <T> asList(vararg ts: T): List<T> {
    val result = ArrayList<T>()
    for (t in ts) // ts is an Array
        result.add(t)
    return result
}

fun main() {
    val list = asList(1.3, 45, 6)
    println(list)
}