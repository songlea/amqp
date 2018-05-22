package com.songlea.demo.amqp.controller

import com.songlea.demo.amqp.model.ArticleModel
import com.songlea.demo.amqp.service.BlogService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.ModelAndView

/**
 * 首页Controller
 *
 * @author Song Lea
 */
@Controller
class IndexController @Autowired constructor(private val blogService: BlogService) {

    // 首页
    @RequestMapping(value = ["/index"], method = [RequestMethod.GET])
    fun index(): ModelAndView {
        val list: List<ArticleModel> = blogService.selectAllArticles()
        val modelAndView = ModelAndView("index")
        modelAndView.addObject("articles", list)
        return modelAndView
    }

    // 加载具体的文章内容
    @RequestMapping(value = ["/articles/{id}"], method = [RequestMethod.GET])
    fun selectArticleById(@PathVariable("id") id: Int): ModelAndView {
        val article: ArticleModel = blogService.selectArticleById(id)
        val modelAndView = ModelAndView("blog")
        modelAndView.addObject("article", article)
        return modelAndView
    }
}