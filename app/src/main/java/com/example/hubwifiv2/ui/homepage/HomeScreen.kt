package com.example.hubwifiv2.ui.homepage

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hubwifiv2.DevicesButtons
import com.example.hubwifiv2.TCPTest
import com.example.hubwifiv2.utils.ble.BluetoothScanner
import com.example.hubwifiv2.utils.tcp.TCPClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
@Composable
fun HomeScreen(
    tcpClient: TCPClient,
    context: Context,
    bluetoothScanner: BluetoothScanner,
    bluetoothResults: Set<BluetoothDevice>
) {
    var enableRefresh by remember { mutableStateOf(true) }
    var showLoader by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        TCPTest(tcpClient, context)
        DevicesButtons(context)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    bluetoothScanner.startScan()
                    enableRefresh = false
                    showLoader = true

                    CoroutineScope(Dispatchers.Default).launch {
                        delay(10000)
                        bluetoothScanner.stopScan()

                        enableRefresh = true
                        showLoader = false
                    }
                },
                enabled = enableRefresh
            ) {
                Text(text = "Rescan")
            }
            if (showLoader)
                CircularProgressIndicator(
                    modifier = Modifier.width(24.dp)
                )
        }


        LazyColumn{
            items(bluetoothResults.toList()) { device ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp, horizontal = 6.dp)
                    ) {
                        Text(text = "address")
                        device.address?.let { it1 -> Text(text = it1) }

                        Text(text = "name")
                        device.name?.let { it1 -> Text(text = it1) }
                    }
                }
            }
        }

    }
    
}