package com.songlea.demo.amqp.dao

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.util.*

/**
 * 博客Dao层
 *
 * @author Song Lea
 */
@Repository
class BlogDao @Autowired constructor(private val jdbcTemplate: JdbcTemplate) {

    // 保存文章
    fun saveArticle(title: String, content: String, userId: Int): Int {
        return jdbcTemplate.update("insert into amqp_article(title, content, user_id, create_time) VALUES (?,?,?,?)",
                title, content, userId, Date())
    }
}