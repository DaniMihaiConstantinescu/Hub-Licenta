package com.example.hubwifiv2.utils.dataClasses

data class TCPInitalizeMessage(
    var messageType:String = "initialize",
    var mac: String,
)
