package com.songlea.demo.amqp.controller

import com.songlea.demo.amqp.message.ScheduleSender
import com.songlea.demo.amqp.model.ResponseData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.servlet.http.HttpServletResponse

/**
 * 主页Controller
 *
 * @author Song Lea
 */
@Controller
@RequestMapping("/home")
class HomeController(@Autowired private val scheduleSender: ScheduleSender) {

    @Value("\${web.upload-path}")
    private var filePath: String = ""

    // 主页
    @RequestMapping(value = ["/index"], method = [RequestMethod.GET])
    fun index() = "home"

    // 发送信息(暂存到RabbitMQ中)
    @RequestMapping(value = ["/chat"], method = [RequestMethod.POST])
    @ResponseBody
    fun chat(message: String?): ResponseData {
        if (message.isNullOrBlank())
            return ResponseData(ResponseData.HOME_PAGE_ERROR_CODE, ResponseData.NO_MESSAGE)
        scheduleSender.sendChatMessage(message!!)
        // 将完成时间返回到前台
        return ResponseData(SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()))
    }

    // 文件上传
    @RequestMapping(value = ["/upload"], method = [RequestMethod.POST])
    @ResponseBody
    fun upload(file: MultipartFile?): ResponseData {
        if (file == null || file.size <= 0)
            return ResponseData(ResponseData.HOME_PAGE_ERROR_CODE, ResponseData.NO_FILE)
        // use相当于jdk7中的try-resources块不需要手动关闭流
        FileOutputStream(File(filePath + file.originalFilename))
                .buffered().use {
                    it.write(file.bytes)
                }
        return ResponseData(null)
    }

    // 文件下载
    @RequestMapping(value = ["/download"], method = [RequestMethod.GET])
    fun download(response: HttpServletResponse, fileName: String?) {
        if (fileName.isNullOrBlank()) return
        response.outputStream.use {
            it.write(FileInputStream(File(filePath + fileName)).readBytes())
        }
    }
}