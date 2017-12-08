package com.vinson.word.hskjson

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.vinson.common.awaitBgState
import com.vinson.common.maskDataFile
import com.vinson.common.postBg
import java.io.File

/**
 * Created by Vinson on 2017/12/7.
 * e-mail: wei2006004@foxmail.com
 */

fun main(args: Array<String>) {
    findErrorWord()
}

/**
 * 从data/json文件夹中找出有问题的数据并mask
 */
fun findErrorWord() {
    traverseHskDataList(2..6) { _, list ->
        list.forEachIndexed { index, data ->
            postBg {
                val word = data.getHskWord()
                if (word.pinyin.isEmpty()) {
                    maskDataFile("data/error", "pinyin", data)
                }
                if (word.morphs.isEmpty()) {
                    maskDataFile("data/error", "morphs", data)
                }
                if (word.phrases.isEmpty()) {
                    maskDataFile("data/error", "phrase", data)
                }
                if (word.samples.isEmpty()) {
                    maskDataFile("data/error", "sample", data)
                }
                awaitBgState(index + 1)
            }
        }
    }
    Thread.sleep(1000)
    println("submit ")
}

private fun HskData.getHskWord() = jacksonObjectMapper().readValue<HskWord>(File("data/hsk$level", fileName()))
