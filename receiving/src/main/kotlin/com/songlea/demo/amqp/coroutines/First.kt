package com.songlea.demo.amqp.coroutines

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

/**
 * 本质上协程是轻量级的线程，它们在某些 CoroutineScope 上下文中与 launch 协程构建器一起启动
 */
fun main() {
    // 这里我们在 GlobalScope 中启动了一个新的协程，这意味着新协程的生命周期只受整个应用程序的生命周期限制。
    // 在后台启动一个新的协程并继续
    GlobalScope.launch {
        // 非阻塞的等待 1 秒钟（默认时间单位是毫秒）
        delay(1000L)
        // 在延迟后打印输出
        println("World!")
    }
    // 协程已在等待时主线程还在继续
    println("Hello,")
    // 阻塞主线程 2 秒钟来保证 JVM 存活
    Thread.sleep(2000L)

    // 可以将 GlobalScope.launch { …… } 替换为 thread { …… }，将 delay(……) 替换为 Thread.sleep(……) 达到同样目的
    thread {
        Thread.sleep(1000L)
        println("World2!")
    }
    println("Hello2,")
    Thread.sleep(2000L)
}