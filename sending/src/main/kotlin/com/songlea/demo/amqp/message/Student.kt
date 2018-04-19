package com.songlea.demo.amqp.message

import java.io.Serializable

/**
 * 传递的数据(java对象)
 *
 * @author Song Lea
 */
class Student(private var id: Int, private var name: String) : Serializable {

    override fun toString(): String {
        return "Student(id=$id, name='$name')"
    }
}