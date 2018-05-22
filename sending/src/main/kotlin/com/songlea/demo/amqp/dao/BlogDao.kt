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

    // 加载文章列表(用户加载需要分页)
    fun selectUserArticles(userId: Int, start: Int, limit: Int): List<ArticleModel> {
        return jdbcTemplate.query("SELECT a.id, a.title, a.create_time, u.user_name FROM amqp_article a," +
                " amqp_user u where a.user_id = u.id and user_id = ? limit ?,?",
                arrayOf(userId, start, limit), RowMapper { rs, _ ->
            val record = ArticleModel()
            record.id = rs.getInt("id")
            record.title = rs.getString("title") ?: ProjectCommonUtil.DEFAULT_EMPTY
            record.userId = userId
            record.createTime = ProjectCommonUtil.formatData(rs.getTimestamp("create_time"))
            record.userName = rs.getString("user_name") ?: ProjectCommonUtil.DEFAULT_EMPTY
            return@RowMapper record
        })
    }

    // 加载所有文章列表
    fun selectAllArticles(): List<ArticleModel> {
        return jdbcTemplate.query("SELECT a.id, a.title, a.create_time, u.user_name,a.user_id FROM amqp_article a," +
                " amqp_user u where a.user_id = u.id", RowMapper { rs, _ ->
            val record = ArticleModel()
            record.id = rs.getInt("id")
            record.title = rs.getString("title") ?: ProjectCommonUtil.DEFAULT_EMPTY
            record.userId = rs.getInt("user_id")
            record.createTime = ProjectCommonUtil.formatData(rs.getTimestamp("create_time"))
            record.userName = rs.getString("user_name") ?: ProjectCommonUtil.DEFAULT_EMPTY
            return@RowMapper record
        })
    }

    // 加载文章详情
    fun selectArticleById(id: Int): ArticleModel {
        val list: List<ArticleModel> = jdbcTemplate.query(
                "select a.id,a.title,a.content,a.create_time,u.user_name from amqp_article a,amqp_user u " +
                        "where a.user_id = u.id and a.id = ?", arrayOf(id), RowMapper { rs, _ ->
            val record = ArticleModel()
            record.id = rs.getInt("id")
            record.title = rs.getString("title") ?: ProjectCommonUtil.DEFAULT_EMPTY
            record.content = rs.getString("content") ?: ProjectCommonUtil.DEFAULT_EMPTY
            record.createTime = ProjectCommonUtil.formatData(rs.getTimestamp("create_time"))
            record.userName = rs.getString("user_name") ?: ProjectCommonUtil.DEFAULT_EMPTY
            return@RowMapper record
        })
        return if (list.isNotEmpty()) list[0] else ArticleModel(ArticleModel.DELETE_CONTENT)
    }

}