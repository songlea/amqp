package com.songlea.demo.amqp.model

import com.songlea.demo.amqp.util.ProjectCommonUtil

// 登录用户表
data class UserModel(var id: Int, var username: String, var email: String, var password: String) {
    // 默认的无参数构造器
    constructor() : this(-1, ProjectCommonUtil.DEFAULT_EMPTY, ProjectCommonUtil.DEFAULT_EMPTY,
            ProjectCommonUtil.DEFAULT_EMPTY)
}