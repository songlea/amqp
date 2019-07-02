package com.songlea.demo.amqp.note.rxjava

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.AsyncSubject
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class RxJavaTests {

    @Test
    fun test1() {
        val asyncSubject = AsyncSubject.create<String>()
        asyncSubject.onNext("asyncSubject1")
        asyncSubject.onNext("asyncSubject2")
        asyncSubject.onNext("asyncSubject3")
        asyncSubject.onComplete()

        asyncSubject.subscribe(object : Observer<String> {
            override fun onComplete() {
                println("asyncSubject onComplete")
            }

            override fun onSubscribe(d: Disposable) {
                println("asyncSubject onSubscribe")
            }

            override fun onError(e: Throwable) {
                println("asyncSubject onError")
            }

            override fun onNext(t: String) {
                println("asyncSubject onNext:$t")
            }
        })
    }
}