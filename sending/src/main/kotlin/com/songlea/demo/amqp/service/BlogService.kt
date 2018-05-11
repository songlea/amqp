package com.songlea.demo.amqp.service

import com.songlea.demo.amqp.dao.BlogDao
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
}