package com.songlea.demo.amqp.dao

import com.songlea.demo.amqp.model.ArticleModel
import com.songlea.demo.amqp.util.ProjectCommonUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
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

    // 加载文章列表
    fun selectArticles(userId: Int, start: Int, limit: Int): List<ArticleModel> {
        return jdbcTemplate.query("SELECT * FROM amqp_article where user_id = ? limit ?,?",
                arrayOf(userId, start, limit), RowMapper { rs, _ ->
            val record = ArticleModel()
            record.id = rs.getInt("id")
            record.title = rs.getString("title") ?: ProjectCommonUtil.DEFAULT_EMPTY
            // 这里不加载具体的内容,点击后加载
            // record.content = rs.getString("content") ?: ProjectCommonUtil.DEFAULT_EMPTY
            record.userId = userId
            record.createTime = ProjectCommonUtil.formatData(rs.getTimestamp("create_time"))
            return@RowMapper record
        })
    }

    // 加载文章详情
    fun selectArticleById(id: Int): ArticleModel {
        val list: List<ArticleModel> = jdbcTemplate.query(
                "select * from amqp_article where id = ?", arrayOf(id), RowMapper { rs, _ ->
            val record = ArticleModel()
            record.id = rs.getInt("id")
            record.title = rs.getString("title") ?: ProjectCommonUtil.DEFAULT_EMPTY
            record.content = rs.getString("content") ?: ProjectCommonUtil.DEFAULT_EMPTY
            record.createTime = ProjectCommonUtil.formatData(rs.getTimestamp("create_time"))
            return@RowMapper record
        })
        return if (list.isNotEmpty()) list[0] else ArticleModel(ArticleModel.DELETE_CONTENT)
    }
}