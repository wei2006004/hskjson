package com.vinson.common

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.vinson.word.hskjson.*
import java.io.*

/**
 * Created by Vinson on 2017/12/4.
 * e-mail: wei2006004@foxmail.com
 */

fun maskDataFile(path: String, tag: String, data: HskData) {
    val mapper = jacksonObjectMapper()
    val json = mapper.writeValueAsString(data)
    saveFile(json, path, tag + "_${data.level}_" + data.order + "_" + data.word)
}

fun saveFile(text:String, path: String, fileName: String) = saveFile(ByteArrayInputStream(text.toByteArray()), path, fileName)

fun saveFile(inputStream: InputStream, path: String, fileName: String) {

    var os: OutputStream? = null
    try {
        // 2、保存到临时文件
        // 1K的数据缓冲
        val bs = ByteArray(1024)
        // 读取到的数据长度
        var len: Int
        // 输出的文件流保存到本地文件
        val tempFile = File(path)
        if (!tempFile.exists()) {
            tempFile.mkdirs()
        }
        os = FileOutputStream(tempFile.path + File.separator + fileName)

        len = inputStream.read(bs)
        // 开始读取
        while (len != -1) {
            os.write(bs, 0, len)
            len = inputStream.read(bs)
        }

    } catch (e: IOException) {
        e.printStackTrace()
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        // 完毕，关闭所有链接
        try {
            os?.close()
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}