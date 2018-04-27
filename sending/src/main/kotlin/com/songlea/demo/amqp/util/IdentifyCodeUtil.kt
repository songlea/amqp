package com.songlea.demo.amqp.util

import org.apache.tomcat.util.http.fileupload.IOUtils
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.IOException
import java.io.OutputStream
import java.util.*
import javax.imageio.ImageIO

/**
 * 生成随机验证码工具类
 *
 * @author Song Lea
 */
class IdentifyCodeUtil(
        private var width: Int,// 图片的宽度
        private var height: Int,// 图片的高度
        private var codeCount: Int,// 验证码字符个数
        private var lineCount: Int) { // 验证码干扰线数

    private var code: String = ""  // 验证码(需要)
    private val buffImg: BufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB) // Image对象
    private val random = Random()

    // 生成验证码图片的实现
    private fun createImage() {
        // 字体的宽度与高度
        val fontWidth: Int = width / codeCount
        val fontHeight: Int = height - 5
        val codeY: Int = height - 8
        // 生成Graphics2D对象
        val graphics: Graphics2D = buffImg.createGraphics()
        // 设置背景色
        graphics.color = getRandomColor(200, 250)
        graphics.fillRect(0, 0, width, height)
        // 设置字体
        graphics.font = getRandomFont(fontHeight)
        // 设置干扰线
        for (i in 0 until lineCount) {
            val xs = random.nextInt(width)
            val ys = random.nextInt(height)
            val xe = xs + random.nextInt(width)
            val ye = ys + random.nextInt(height)
            graphics.color = getRandomColor(1, 255)
            graphics.drawLine(xs, ys, xe, ye)
        }
        // 添加噪点
        val area = (0.01f * width.toFloat() * height).toInt()
        for (i in 0 until area) {
            buffImg.setRGB(random.nextInt(width), random.nextInt(height), random.nextInt(255))
        }
        // 得到随机字符
        this.code = getRandomStr(codeCount)
        for (i in 0 until codeCount) {
            // 为字符设置随机的颜色
            graphics.color = getRandomColor(1, 255)
            // strRand为要画出来的东西,x和y表示要画的东西最左侧字符的基线位于此图形上下文坐标系的(x, y)位置处
            graphics.drawString(this.code.substring(i, i + 1), i * fontWidth + 3, codeY)
            // 旋转指定角度
            graphics.rotate(Math.toRadians(random.nextInt(5).toDouble()), (width / 2).toDouble(), (height / 2).toDouble())
        }
        // 释放资源
        graphics.dispose()
    }

    // 得到随机字符
    private fun getRandomStr(n: Int): String {
        val str = StringBuilder()
        val len = BASE_CODE_CHAR.length - 1
        var r: Double
        for (i in 0 until n) {
            r = Math.random() * len
            str.append(BASE_CODE_CHAR[r.toInt()])
        }
        return str.toString()
    }

    // 得到随机颜色
    private fun getRandomColor(fc: Int, bc: Int): Color {
        val r = fc + random.nextInt(bc - fc)
        val g = fc + random.nextInt(bc - fc)
        val b = fc + random.nextInt(bc - fc)
        return Color(r, g, b)
    }

    // 产生随机字体
    private fun getRandomFont(size: Int): Font {
        val random = Random()
        val font: Array<Font> = arrayOf(
                Font("Ravie", Font.PLAIN, size),
                Font("Consolas", Font.PLAIN, size),
                Font("Fixedsys", Font.PLAIN, size),
                Font("Wide Latin", Font.PLAIN, size))
        return font[random.nextInt(4)]
    }

    // 写验证码到输出流
    @Throws(IOException::class)
    fun write(outputStream: OutputStream) {
        try {
            ImageIO.write(buffImg, "png", outputStream)
        } finally {
            IOUtils.closeQuietly(outputStream)
        }
    }

    // 返回验证码
    fun getCode(): String {
        return code.toLowerCase()
    }

    companion object {

        private const val BASE_CODE_CHAR = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefhijklmnpqrstuvwxyz2345678"
    }

    init {
        // 执行初始化
        createImage()
    }
}
