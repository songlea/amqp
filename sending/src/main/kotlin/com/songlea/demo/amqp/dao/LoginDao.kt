package com.songlea.demo.amqp.dao

import com.songlea.demo.amqp.model.UserModel
import com.songlea.demo.amqp.util.ProjectCommonUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

/**
 * 登录Dao层
 *
 * @author Song Lea
 */
@Repository
class LoginDao @Autowired constructor(private val jdbcTemplate: JdbcTemplate) {

    // 根据用户名与密码判断用户表是否有记录
    fun getUserByUsernameAndPass(username: String, password: String): UserModel? {
        val list: List<UserModel> = jdbcTemplate.query("SELECT * FROM amqp_user where user_name = ? and password = ?",
                arrayOf(username, password), RowMapper { rs, _ ->
            val record = UserModel()
            record.id = rs.getInt("id")
            record.username = rs.getString("user_name") ?: ProjectCommonUtil.DEFAULT_EMPTY
            record.email = rs.getString("email") ?: ProjectCommonUtil.DEFAULT_EMPTY
            record.password = rs.getString("password") ?: ProjectCommonUtil.DEFAULT_EMPTY
            return@RowMapper record
        })
        return if (list.isEmpty()) null else list[0]
    }

    // 校验用户名是否重复
    fun checkRepeatUsername(username: String): Boolean {
        return jdbcTemplate.queryForList("SELECT id FROM amqp_user where user_name = ? ", username).size > 0
    }

    // 校验邮箱是否重复
    fun checkRepeatEmail(email: String): Boolean {
        return jdbcTemplate.queryForList("SELECT id FROM amqp_user where email = ? ", email).size > 0
    }

    // 保存注册用户信息
    fun saveUserModel(username: String, email: String, password: String): Int {
        return jdbcTemplate.update("insert into amqp_user(user_name,email,password) values(?,?,?)",
                username, email, password)
    }

    // 通过邮箱地址来重置密码
    fun resetPassword(email: String, password: String): Int {
        return jdbcTemplate.update("update amqp_user set password = ? where email = ?",
                password, email)
    }

    // 校验用户名与邮箱是否对应
    fun existUsernameAndEmail(username: String, email: String): Boolean {
        return jdbcTemplate.queryForList("SELECT id FROM amqp_user where user_name = ? and email = ?",
                username, email).size > 0
    }
}