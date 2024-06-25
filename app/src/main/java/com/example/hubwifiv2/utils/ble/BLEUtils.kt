package com.example.hubwifiv2.utils.ble

import android.util.Log

object BLEUtils {
    fun extractType(input: String): String? {
        val pattern = "\\{type:\\s*([^}]+)\\}".toRegex()
        val matchResult = pattern.find(input)
        return matchResult?.groupValues?.getOrNull(1)
    }

}