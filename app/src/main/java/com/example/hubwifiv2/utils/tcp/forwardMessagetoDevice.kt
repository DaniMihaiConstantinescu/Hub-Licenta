package com.example.hubwifiv2.utils.tcp

import android.content.Context
import com.example.hubwifiv2.utils.ble.BluetoothManager
import com.example.hubwifiv2.utils.dataClasses.tcp.TCPMessage

fun forwardMessageToDevice(context: Context, message: TCPMessage){
    val bluetoothManager = BluetoothManager(context, false, setType = {}, message.settings.toString())
    bluetoothManager.scanForDevice(message.deviceMac)
}