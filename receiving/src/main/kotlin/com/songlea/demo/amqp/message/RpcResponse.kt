package com.songlea.demo.amqp.message

import com.alibaba.fastjson.JSON

/**
 * RPC调用返回数据格式
 */
data class RpcResponse(private var code: Int, private var message: String)

/**
 * 工具类
 */
object ReceiverUtils {

    // 对象转换为json字符串
    fun toJSONString(rpcResponse: RpcResponse): String {
        return JSON.toJSONString(rpcResponse)
    }
}