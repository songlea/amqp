package com.songlea.demo.amqp.note.flux

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import reactor.core.publisher.Flux
import java.time.Duration
import java.time.temporal.ChronoUnit

@RunWith(JUnit4::class)
class FluxTests {

    /**
     * Flux相当于一个RxJava Observable观察者
     * 可以把生产的消息Publisher,推送给消费者Subscribe
     */
    @Test
    fun flux() {
        Flux.just("1", "2", "3").subscribe(::println)
        Flux.fromArray(arrayOf(1, 2, 3)).subscribe(::println)
        Flux.fromStream(listOf(1, 2, 3, 4).stream()).subscribe(::println)
        Flux.empty<String>().subscribe(::println)
        Flux.range(1, 10).subscribe(::println)
        Flux.interval(Duration.of(10, ChronoUnit.SECONDS)).subscribe(::println)

    }
}