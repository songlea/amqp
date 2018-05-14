package com.songlea.demo.amqp.model

/**
 * 封装返回响应数据与定义常量
 *
 * @author Song Lea
 */
data class ResponseData(var code: Int, var message: String?, var data: Any?) {

    constructor(data: Any?) : this(0, SUCCESS, data)

    enum class ExceptionEnum constructor(private val code: Int, private val msg: String) {
        // 用户登录
        NO_USER_NAME(1, "请输入用户名！"),
        LONG_USER_NAME(1, "用户名长度至多32位！"),
        NO_PASSWORD(1, "请输入密码！"),
        SHORT_PASSWORD(1, "密码长度应至少6位！"),
        NO_VERIFICATION_CODE(1, "请输入验证码！"),
        ERROR_VERIFICATION_CODE(1, "验证码不正确！"),
        ERROR_USER_OR_PASSWORD(1, "用户名或密码不正确！"),
        // 用户注册
        REPEAT_USER_NAME(3, "您的用户名已被注册！"),
        REPEAT_EMAIL(3, "您的邮箱地址已被注册！"),
        NO_EMAIL(3, "请输入邮箱地址！"),
        INVALID_EMAIL(3, "邮箱地址格式不正确！"),
        // 找加/重置密码
        NO_REGISTER_EMAIL(3, "邮箱地址未被注册！"),
        NO_MAPPING_USER_AND_EMAIL(3, "您输入的注册用户名与邮箱地址不对应！"),
        // 修改密码
        OVER_TIME_LOGIN(4, "登录已过期，请重新登录！"),
        SAME_PASSWORD(4, "新密码不能与旧密码相同！"),
        NO_OLD_PASSWORD(4, "请输入旧密码！"),
        NO_NEW_PASSWORD(4, "请输入新密码！"),
        WRONG_OLD_PASSWORD(4, "您输入的旧密码不正确！"),
        // 文章发布/文件上传
        NO_MESSAGE(5, "不能发送空白信息！"),
        NO_FILE(5, "请选择上传文件！"),
        NO_BLOG_TITLE(5, "请输入文章标题！"),
        LONG_BLOG_TITLE(5, "文章标题长度至多64位！"),
        NO_BLOG_CONTENT(5, "请输入文章内容！"),
        // 异常处理
        EXCEPTION_SYSTEM_BUSY(100, "系统正忙，请稍后重试！"),
        EXCEPTION_METHOD_NOT_SUPPORTED(101, "请求的方式不对(POST/GET)！"),
        EXCEPTION_LACK_PARAMETER(102, "请求的参数不完整！"),
        EXCEPTION_ITF_EMPTY_DATA(103, "HTTP接口调用未返回数据！"),
        EXCEPTION_ITF_CALL(104, "HTTP接口调用失败！"),
        EXCEPTION_ITF_CALL_TIMEOUT(105, "HTTP接口调用超时！"),
        EXCEPTION_ARGUMENT_TYPE_MISMATCH(106, "请求的参数格式不匹配！"),
        EXCEPTION_MEDIA_TYPE_NOT_ACCEPTABLE(107, "请求的MINE类型不接受！"),
        EXCEPTION_MEDIA_TYPE_NOT_SUPPORTED(108, "请求的MIME类型不支持！");

        fun getResult(): ResponseData {
            return ResponseData(this.code, this.msg, null)
        }
    }

    companion object {
        const val SUCCESS = "操作成功！"
        // 登录界面
        const val LOGIN_URL = "/login/index"
        const val VERIFICATION_CODE_NAME = "VerificationCode"
        const val USER_MODEL = "userModel"
        // Cookie设置
        const val COOKIE_NAME = "AMQP-COOKIE"
        const val COOKIE_COMMENT = "My AMQP Application's Login Cookie Comment"
        const val COOKIE_PATH = "/"
        const val COOKIE_HTTP_ONLY = true
        const val COOKIE_SECURE = false
        const val COOKIE_MAX_AGE = 30 * 60
    }

}