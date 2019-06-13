package com.songlea.demo.amqp.note

const val eps = 1E-10

tailrec fun findFixPoint(x: Double = 1.0): Double = if (Math.abs(x - Math.cos(x)) < eps) x else findFixPoint(Math.cos(x))

// 相当于以下代码
/*
private fun findFixPoint(): Double {
    var x = 1.0
    while (true) {
        val y = Math.cos(x)
        if (Math.abs(x - y) < eps) return x
        x = Math.cos(x)
    }
}
*/

fun main() {
    println(findFixPoint())
}