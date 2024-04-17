package com.example.hubwifiv2.utils.tcp

import com.example.hubwifiv2.utils.dataClasses.tcp.TCPInitalizeMessage
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

fun sendInitializeMessageTCP(
    tcpClient: TCPClient,
    macAddress: String
) {

    val message = TCPInitalizeMessage("initialize", macAddress)
    val gson = Gson()
    GlobalScope.launch {
        tcpClient.sendDataToServer(gson.toJson(message))
    }
}