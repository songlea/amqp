package com.songlea.demo.amqp.note

import com.songlea.demo.amqp.trie.AhoCorasickDoubleArrayTrie
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

object BadWordUtil {

    // 从文件中获取敏感词库
    fun readWordByLine(path: String): List<String> {
        val keyWordList = ArrayList<String>()
        File(path).forEachLine {
            if (it != "") {
                keyWordList.add(it.trim())
            }
        }
        return keyWordList
    }

}

fun main() {

    val badWordList: List<String> = BadWordUtil.readWordByLine("D:\\BadWord.txt")
    println(badWordList.size)

    var text = File("D:\\text.txt").readText()

    /*
    val dat = DoubleArrayTrie()
    println("是否错误:${dat.build(badWordList)}")

    val exactMatchResult = dat.exactMatchSearch("太多的伤感運營组情怀也许只局限于饲养基地荧幕中的情节，主人公尝试着去用某种方式渐渐的很潇洒地释自杀指南怀那些自己经历的伤感。"
            + "然后法.轮.功我们的扮演的角色就是跟随着主人公的喜红客联盟 怒哀乐而过于牵强的把自己的情感也附加于银幕情节中，然后感动就流泪，"
            + "难过就躺在某一个人的怀里尽情的阐述心扉或者手机卡复制器一个人一杯红酒一部电影在夜三.级.片 深人静大波波的晚上，关上电话静静的发呆着封神榜。")
    println(badWordList[exactMatchResult])

    val commonResult = dat.commonPrefixSearch("太多的伤感運營组情怀也许只局限于饲养基地荧幕中的情节，主人公尝试着去用某种方式渐渐的很潇洒地释自杀指南怀那些自己经历的伤感。"
            + "然后法.轮.功我们的扮演的角色就是跟随着主人公的喜红客联盟 怒哀乐而过于牵强的把自己的情感也附加于银幕情节中，然后感动就流泪，"
            + "难过就躺在某一个人的怀里尽情的阐述心扉或者手机卡复制器一个人一杯红酒一部电影在夜三.级.片 深人静大波波的晚上，关上电话静静的发呆着封神榜。")
    commonResult.forEach {
        println(badWordList[it])
    }
    */
    val ahoCorasickDoubleArrayTrie = AhoCorasickDoubleArrayTrie<String>()
    val dictionaryMap = TreeMap<String, String>()
    badWordList.forEach {
        dictionaryMap[it] = it
    }
    ahoCorasickDoubleArrayTrie.build(dictionaryMap)
    println("Parsing document which contains ${text.length} characters, with a dictionary of ${dictionaryMap.size} words.")

    val start = System.currentTimeMillis()
    /*
    ahoCorasickDoubleArrayTrie.parseText(text, AhoCorasickDoubleArrayTrie.IHit<String> { begin, end, value ->
        println("begin:$begin; end:$end; value:$value")
    })
    */
    val result: List<AhoCorasickDoubleArrayTrie.Hit<String>> = ahoCorasickDoubleArrayTrie.parseText(text)
    result.forEach {
        text = text.replaceRange(IntRange(it.begin, it.end), "*".repeat(it.end - it.begin + 1))
    }
    println(text)
    println("take times:${System.currentTimeMillis() - start}")
}