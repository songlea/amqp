package com.songlea.demo.amqp.model

/**
 * 封装返回响应数据与定义常量
 *
 * @author Song Lea
 */
data class ResponseData(var code: Int, var message: String?, var url: String?, var data: Any?) {

    companion object {
        const val SUCCESS = "操作成功！"
        const val LOGIN_PAGE_ERROR_CODE = 1
        const val HOME_PAGE_ERROR_CODE = 2
        // 登录界面
        const val LOGIN_URL = "/login/index"
        const val VERIFICATION_CODE_NAME = "VerificationCode"
        const val USERNAME = "admin"
        const val PASSWORD = "password"
        const val NO_USER_NAME = "请输入用户名！"
        const val NO_PASSWORD = "请输入密码！"
        const val NO_VERIFICATION_CODE = "请输入验证码！"
        const val ERROR_VERIFICATION_CODE = "验证码不正确！"
        const val ERROR_USER_OR_PASSWORD = "用户名或密码不正确！"
        // 主页
        const val NO_MESSAGE = "不能发送空白信息"
        const val NO_FILE = "请选择上传文件"
        // Cookie设置
        const val COOKIE_NAME = "AMQP-COOKIE"
        const val COOKIE_COMMENT = "My AMQP Application's Login Cookie Comment"
        const val COOKIE_PATH = "/"
        const val COOKIE_HTTP_ONLY = true
        const val COOKIE_SECURE = false
        const val COOKIE_MAX_AGE = 30 * 60
    }

    constructor(data: Any?) : this(0, SUCCESS, null, data)

    constructor(code: Int, message: String?) : this(code, message, null, null)

    // 异常处理
    enum class ExceptionEnum constructor(private val code: Int, private val msg: String) {

        EXCEPTION_SYSTEM_BUSY(100, "系统正忙，请稍后重试！"),
        EXCEPTION_METHOD_NOT_SUPPORTED(101, "请求的方式不对(POST/GET)！"),
        EXCEPTION_LACK_PARAMETER(102, "请求的参数不完整！"),
        EXCEPTION_ITF_EMPTY_DATA(103, "HTTP接口调用未返回数据！"),
        EXCEPTION_ITF_CALL(104, "HTTP接口调用失败！"),
        EXCEPTION_ITF_CALL_TIMEOUT(105, "HTTP接口调用超时！"),
        EXCEPTION_ARGUMENT_TYPE_MISMATCH(106, "请求的参数格式不匹配！"),
        EXCEPTION_MEDIA_TYPE_NOT_ACCEPTABLE(107, "请求的MINE类型不接受！"),
        EXCEPTION_MEDIA_TYPE_NOT_SUPPORTED(108, "请求的MIME类型不支持！");

        fun getResult(url: String): ResponseData {
            return ResponseData(this.code, this.msg, url, null)
        }
    }
}