@file:Suppress("UnstableApiUsage")

package com.songlea.demo.amqp.note.guava

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import com.songlea.demo.amqp.note.rxjava.RxJavaTests
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class GuavaTests {

    @Subscribe
    private fun sendMessageByMail(message: String) {
        println("邮件发送一条数据:$message")
    }

    @Subscribe
    private fun sendMessageByPhone(message: String) {
        println("短消息发送一条数据:$message")
    }

    @Test
    fun test2() {
        val event = EventBus()
        event.register(RxJavaTests())
        event.post("hi:boys")

    }
}