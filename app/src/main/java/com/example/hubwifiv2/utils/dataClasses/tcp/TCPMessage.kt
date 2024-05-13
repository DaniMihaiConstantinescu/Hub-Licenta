package com.example.hubwifiv2.utils.dataClasses.tcp

data class TCPMessage(
    var settings: Map<String, String>,
    var deviceMac: String
)
