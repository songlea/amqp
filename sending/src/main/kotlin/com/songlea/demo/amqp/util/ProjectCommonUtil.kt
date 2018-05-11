package com.songlea.demo.amqp.util

import org.apache.commons.codec.binary.Hex
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import java.security.MessageDigest
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.Random


/**
 * 项目工具类
 *
 * @author SongLea
 */
object ProjectCommonUtil {

    private val Logger: Logger = LoggerFactory.getLogger(ProjectCommonUtil::class.java)
    private val threadPoolExecutor: ThreadPoolExecutor = threadPoolExecutor()

    const val DEFAULT_EMPTY = ""
    const val BASE_CODE_STR = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefhijklmnpqrstuvwxyz2345678"

    private const val CORE_THREAD_POOL_SIZE = 5
    private const val MAX_THREAD_POOL_SIZE = 10
    private const val KEEP_ALIVE_TIME = 60
    private const val QUEUE_CAPACITY_SIZE = 100

    /*
     * 当池子大小小于corePoolSize，就新建线程，并处理请求
     * 当池子大小等于corePoolSize，把请求放入workQueue中，池子里的空闲线程就去workQueue中取任务并处理
     * 当workQueue放不下任务时，就新建线程入池，并处理请求，如果池子大小撑到了maximumPoolSize，就用RejectedExecutionHandler来做拒绝处理
     * 当池子的线程数大于corePoolSize时，多余的线程会等待keepAliveTime长时间，如果无请求可处理就自行销毁
     * CallerRunsPolicy:它直接在execute方法的调用线程中运行被拒绝的任务；如果执行程序已关闭，则会丢弃该任务
     */
    private fun threadPoolExecutor(): ThreadPoolExecutor {
        return ThreadPoolExecutor(CORE_THREAD_POOL_SIZE, MAX_THREAD_POOL_SIZE, KEEP_ALIVE_TIME.toLong(), TimeUnit.SECONDS,
                ArrayBlockingQueue(QUEUE_CAPACITY_SIZE), ThreadPoolExecutor.CallerRunsPolicy())
    }

    // SHA256(安全Hash算法,针对密码处理)
    fun getStringBySHA256(str: String): String {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        return Hex.encodeHexString(messageDigest.digest(str.toByteArray(Charsets.UTF_8)))
    }

    // 发送邮件
    fun mailNotice(mailSender: JavaMailSender, from: String, emails: Array<String>, subject: String, text: String) {
        threadPoolExecutor.execute({
            Thread.currentThread().setUncaughtExceptionHandler { _, e -> Logger.error("异步发送邮件异常！", e) }
            val message = SimpleMailMessage()
            message.setFrom(from)
            message.setTo(*emails)
            message.setSubject(subject)
            message.setText(text)
            mailSender.send(message)
            Logger.info("异步发送邮件成功，收件人：{}", emails.joinToString())
        })
    }

    // 随机密码,默认8位(字母加数字)
    fun randomPassword(length: Int = 8): String {
        val random = Random()
        val result = StringBuilder()
        for (i in 0 until length)
            result.append(BASE_CODE_STR[random.nextInt(BASE_CODE_STR.length)])
        return result.toString()
    }
}