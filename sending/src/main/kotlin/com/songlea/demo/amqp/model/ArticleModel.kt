package com.songlea.demo.amqp.model

import com.songlea.demo.amqp.util.ProjectCommonUtil

/**
 * 文章实体
 *
 * @author Song Lea
 */
data class ArticleModel(var id: Int, var title: String, var content: String, var userId: Int, var createTime: String) {

    // 默认的无参数构造器
    constructor() : this(-1, ProjectCommonUtil.DEFAULT_EMPTY,
            ProjectCommonUtil.DEFAULT_EMPTY, -1, ProjectCommonUtil.DEFAULT_EMPTY)

    constructor(content: String) : this(-1, ProjectCommonUtil.DEFAULT_EMPTY, content,
            -1, ProjectCommonUtil.DEFAULT_EMPTY)

    companion object {
        const val DELETE_CONTENT: String = "<strong>文章已不存在<strong>"
    }
}