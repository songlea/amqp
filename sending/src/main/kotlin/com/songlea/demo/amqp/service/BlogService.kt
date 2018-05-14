package com.songlea.demo.amqp.service

import com.songlea.demo.amqp.dao.BlogDao
import com.songlea.demo.amqp.model.ArticleModel
import com.songlea.demo.amqp.model.ResponseData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

/**
 * 博客Service层
 *
 * @author Song Lea
 */
@Service
class BlogService @Autowired constructor(private val blogDao: BlogDao) {

    // 保存文章
    @Transactional(propagation = Propagation.REQUIRED)
    fun saveArticle(title: String, content: String, userId: Int): Int {
        return blogDao.saveArticle(title, content, userId)
    }

    // 加载文章列表
    @Transactional(readOnly = true)
    fun selectArticles(userId: Int, start: Int, limit: Int): ResponseData {
        return ResponseData(blogDao.selectArticles(userId, start, limit))
    }

    // 加载文章具体的内容
    @Transactional(readOnly = true)
    fun selectArticleById(id: Int): ArticleModel {
        return blogDao.selectArticleById(id)
    }
}