package com.songlea.demo.amqp.controller

import com.songlea.demo.amqp.model.ResponseData
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 富文本编辑(写博客)Controller
 *
 * @author Song Lea
 */
@Controller
@RequestMapping("/edit")
class EditController {

    @Value("\${web.upload-path}")
    private var filePath: String = ""

    // 界面
    @RequestMapping(value = ["/index"], method = [RequestMethod.GET])
    fun index() = "edit"

    // 文件上传
    @RequestMapping(value = ["/upload"], method = [RequestMethod.POST])
    @ResponseBody
    fun upload(request: HttpServletRequest, file: MultipartFile?): ResponseData {
        if (file == null || file.size <= 0) {
            // 没有上传文件
            return ResponseData.ExceptionEnum.NO_FILE.getResult()
        }
        // 重命名文件名防止上传文件名重复
        val randomFileName: String = System.currentTimeMillis().toString() + "_" + file.originalFilename
        // use相当于jdk7中的try-resources块不需要手动关闭流
        FileOutputStream(File(filePath + randomFileName))
                .buffered().use {
                    it.write(file.bytes)
                }
        val contentPath: String = if (request.contextPath == "/") "" else request.contextPath
        // 返回文件的访问URL
        return ResponseData(request.scheme + "://" + request.serverName + ":" + request.serverPort
                + contentPath + "/" + randomFileName)
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