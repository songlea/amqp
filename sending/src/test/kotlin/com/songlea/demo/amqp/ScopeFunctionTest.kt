package com.songlea.demo.amqp

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

data class Person(var name: String, var age: Int, var city: String) {
    fun moveTo(newCity: String) {
        city = newCity
    }

    fun incrementAge() {
        age++
    }
}


@RunWith(JUnit4::class)
class ScopeFunctionTest {

    @Test
    fun testLet() {
        val person:Person = Person("Alice", 20, "Amsterdam").let {
            println(it)
            it.moveTo("London")
            it.incrementAge()
            println(it)
            it
        }
        println(person)
    }
}