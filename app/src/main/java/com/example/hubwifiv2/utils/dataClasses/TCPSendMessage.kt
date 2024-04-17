package com.example.hubwifiv2.utils.dataClasses

data class TCPSendMessage(
    var messageType:String = "hub",
    var hubMac: String,
    var deviceMac: String,
    var device: Map<String, String>
)
