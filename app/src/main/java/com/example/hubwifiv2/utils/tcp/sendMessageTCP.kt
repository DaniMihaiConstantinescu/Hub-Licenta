package com.example.hubwifiv2.utils.tcp

import com.example.hubwifiv2.utils.dataClasses.TCPSendMessage
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

fun sendMessageTCP(
    tcpClient: TCPClient,
    macAddress: String,
    deviceMac: String,
    data: Map<String, String>
){
    val message = TCPSendMessage("hub", hubMac = macAddress, deviceMac, data)
    val gson = Gson()
    GlobalScope.launch {
        tcpClient.sendDataToServer(gson.toJson(message))
    }
}