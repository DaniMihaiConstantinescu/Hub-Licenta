package com.example.hubwifiv2.utils.tcp

import android.content.Context
import android.provider.Settings

fun getAndroidId(context: Context): String {
    return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
}