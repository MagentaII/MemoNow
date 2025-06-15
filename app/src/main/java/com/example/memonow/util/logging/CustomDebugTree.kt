package com.example.memonow.util.logging

import android.util.Log
import timber.log.Timber

class CustomDebugTree : Timber.DebugTree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        // 取得目前執行緒的 stack trace
        val trace = Throwable().stackTrace

        // 找到第一個「不是 Timber 也不是這棵 Tree」的呼叫端
        val caller = trace.firstOrNull { element ->
            val cls = element.className
            !cls.startsWith("timber.log.Timber") &&      // 排除 Timber
                    cls != this::class.java.name                 // 排除 CustomDebugTree 自己
        }

        // 組出 [檔名:行號]#方法
        val location = caller?.let {
            "[${it.fileName}:${it.lineNumber}]#${it.methodName}"
        } ?: "[unknown]"

        // 將詳細資訊放進 message
        val newMsg = "$location ➜ $message"

        // 輸出主訊息
        Log.println(priority, tag ?: "Timber", newMsg)

        // 若有例外，一併印出
        t?.let { Log.println(priority, tag ?: "Timber", Log.getStackTraceString(it)) }
    }
}