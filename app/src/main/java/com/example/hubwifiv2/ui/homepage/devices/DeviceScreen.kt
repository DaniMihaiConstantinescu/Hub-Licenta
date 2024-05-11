package com.example.hubwifiv2.ui.homepage.devices

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.hubwifiv2.utils.ble.BluetoothManager
import com.example.hubwifiv2.utils.csv.CSVUtils

@SuppressLint("MissingPermission")
@Composable
fun DeviceScreen(
    address: String
){
    val context = LocalContext.current
    val bluetoothManager = BluetoothManager(context)
    bluetoothManager.scanForDevice(address)

    Column {
        Text(text = address)
        Row {
            Button(onClick = { bluetoothManager.sendMessage("0") }) {
                Text(text = "0")
            }
            Button(onClick = { bluetoothManager.sendMessage("1") }) {
                Text(text = "1")
            }
            Button(onClick = { bluetoothManager.sendMessage("2") }) {
                Text(text = "2")
            }
        }
    }

}