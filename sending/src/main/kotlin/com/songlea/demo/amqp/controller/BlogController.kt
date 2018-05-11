package com.songlea.demo.amqp.controller

import com.songlea.demo.amqp.model.ResponseData
import com.songlea.demo.amqp.model.UserModel
import com.songlea.demo.amqp.service.BlogService
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
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 写博客Controller层
 *
 * @author Song Lea
 */
@Controller
@RequestMapping("/blog")
class BlogController @Autowired constructor(private val blogService: BlogService) {

    @Value("\${web.upload-path}")
    private var filePath: String = ""

    // 界面
    @RequestMapping(value = ["/index"], method = [RequestMethod.GET])
    fun index() = "edit"

    // 文件上传
    @ResponseBody
    @RequestMapping(value = ["/upload"], method = [RequestMethod.POST])
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
        return ResponseData("$contentPath/$randomFileName")
    }

    // 文件下载
    @RequestMapping(value = ["/download"], method = [RequestMethod.GET])
    fun download(response: HttpServletResponse, fileName: String?) {
        if (fileName.isNullOrBlank()) return
        response.outputStream.use {
            it.write(FileInputStream(File(filePath + fileName)).readBytes())
        }
    }

    // 发布文章
    @ResponseBody
    @RequestMapping(value = ["/publish"], method = [RequestMethod.POST])
    fun publish(request: HttpServletRequest, blogTitle: String?, blogContent: String?): ResponseData {
        if (blogTitle.isNullOrBlank())
            return ResponseData.ExceptionEnum.NO_BLOG_TITLE.getResult()
        if (blogTitle!!.length > 64)
            return ResponseData.ExceptionEnum.LONG_BLOG_TITLE.getResult()
        if (blogContent.isNullOrBlank())
            return ResponseData.ExceptionEnum.NO_BLOG_CONTENT.getResult()
        // 从session中获取登录用户
        val userModel: UserModel = request.session.getAttribute(ResponseData.USER_MODEL) as? UserModel
                ?: return ResponseData.ExceptionEnum.OVER_TIME_LOGIN.getResult()
        // 已非空验证
        return if (blogService.saveArticle(blogTitle, blogContent!!, userModel.id) > 0) {
            ResponseData(null)
        } else {
            ResponseData.ExceptionEnum.EXCEPTION_SYSTEM_BUSY.getResult()
        }
    }
}