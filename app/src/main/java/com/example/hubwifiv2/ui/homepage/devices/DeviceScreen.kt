package com.example.hubwifiv2.ui.homepage.devices

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.magnifier
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.hubwifiv2.utils.ble.BluetoothManager

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun DeviceScreen(
    address: String
){
    var startedConnect by remember { mutableStateOf(false) }
    var isConnecting by remember { mutableStateOf(false) }
    var deviceType by remember { mutableStateOf("") }
    var deviceName by remember { mutableStateOf("") }
    
    val context = LocalContext.current
    val bluetoothManager = BluetoothManager(context, setType = {type ->
        isConnecting = false
        deviceType = type
    })

    Column {
        Text(text = address)

        if (!startedConnect || isConnecting) {
            Button(onClick = {
                bluetoothManager.scanForDevice(address)
                startedConnect = true
                isConnecting = true

            }) {
                Text(text = "Connect")
            }
        }

        if (startedConnect) {
            if (isConnecting) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            else {
                Column {
                    Text(text = "Type: $deviceType")
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Device name:")
                        TextField(value = deviceName, onValueChange = {deviceName = it})
                    }
                    Button(onClick = { /*TODO*/ }) {
                        Text(text = "Add Device")
                    }

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
        }
    }

}