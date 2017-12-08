package com.vinson.word.hskjson

/**
 * Created by Vinson on 2017/12/4.
 * e-mail: wei2006004@foxmail.com
 */

const val BASE_PATH = "./data/"

const val PATH_HSK = BASE_PATH + "source/"

const val SUFFIX_TXT = ".txt"

const val TAG_HSK = "hsk"

data class HskData(val level: Int, val word: String, val order: Int)

fun HskData.fileName() = "$order" + "_" + word

data class Mean(val mean: String, val sample: List<WordPair> = listOf())

data class WordPair(val english: String, val chinese: String = "")

data class Morphology(val morph: String, val means: List<Mean> = listOf())

data class HskWord(val data: HskData,
                   val pinyin: String,
                   val phrases: List<WordPair>,
                   val morphs: List<Morphology>,
                   val samples: List<WordPair>)