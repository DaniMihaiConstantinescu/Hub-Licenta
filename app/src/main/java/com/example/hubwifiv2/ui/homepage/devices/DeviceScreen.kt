package com.example.hubwifiv2.ui.homepage.devices

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothProfile
import android.util.Log
import android.widget.Toast
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.hubwifiv2.utils.ble.BluetoothManager

@SuppressLint("MissingPermission")
@Composable
fun DeviceScreen(
    address: String
){
    val context = LocalContext.current
    val bluetoothManager = BluetoothManager(context)

    bluetoothManager.scanForDevice(address)


    Text(text = address)
}