package com.vinson.word.hskjson

import java.io.File

/**
 * Created by Vinson on 2017/12/5.
 * e-mail: wei2006004@foxmail.com
 */

fun traverseHskDataList(traverse: (Int, List<HskData>) -> Unit) = traverseHskDataList(1..6, traverse)

fun traverseHskDataList(range: IntRange, traverse: (Int, List<HskData>) -> Unit) {
    range.forEach { level ->
        val list = mutableListOf<HskData>()
        traverseHskData(level) {
            list.add(it)
        }
        traverse(level, list)
    }
}

fun traverseHskData(level: Int, traverse: (HskData) -> Unit) {
    val file = File(PATH_HSK + TAG_HSK + level + SUFFIX_TXT)
    file.useLines(Charsets.UTF_16LE) {
        val iter = it.iterator()
        while (iter.hasNext()) {
            val data = iter.next().split(",")
            val order = try {
                data[0].toInt()
            } catch (e: Exception) {
                1
            }
            val word = data[1].trim()
            traverse(HskData(level, word, order))
        }
    }
}