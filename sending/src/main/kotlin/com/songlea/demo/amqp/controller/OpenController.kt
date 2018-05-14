package com.songlea.demo.amqp.controller

import com.songlea.demo.amqp.model.ArticleModel
import com.songlea.demo.amqp.service.BlogService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView


/**
 * 对外提供的接口，不需要登录
 *
 * @author Song Lea
 */
@Controller
class OpenController @Autowired constructor(private val blogService: BlogService) {

    // 加载具体的文章内容
    @RequestMapping(value = ["/articles/{id}"], method = [RequestMethod.GET])
    fun selectArticleById(@PathVariable("id") id: Int): ModelAndView {
        val article: ArticleModel = blogService.selectArticleById(id)
        val modelAndView = ModelAndView("blog")
        modelAndView.addObject("title", article.title)
        modelAndView.addObject("content", article.content)
        modelAndView.addObject("createTime", article.createTime)
        return modelAndView
    }
}