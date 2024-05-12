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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hubwifiv2.DevicesButtons
import com.example.hubwifiv2.TCPTest
import com.example.hubwifiv2.ui.dialogs.SimpleConfirmationDialog
import com.example.hubwifiv2.utils.ble.BluetoothScanner
import com.example.hubwifiv2.utils.tcp.TCPClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    tcpClient: TCPClient,
    context: Context,
    navController: NavController,
    bluetoothScanner: BluetoothScanner,
    bluetoothResults: Set<BluetoothDevice>,
    clearResults: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        TCPTest(tcpClient, context)
        DevicesButtons(context)

        BluetoothPart(
            bluetoothScanner,
            bluetoothResults,
            clearResults,
            navController
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun BluetoothPart(
    bluetoothScanner: BluetoothScanner,
    bluetoothResults: Set<BluetoothDevice>,
    clearResults: () -> Unit,
    navController: NavController
) {
    var enableRefresh by remember { mutableStateOf(false) }
    var deviceNames by remember { mutableStateOf(emptyMap<String, String>()) }

    var showDialog by remember { mutableStateOf(false) }
    if (!bluetoothScanner.isBluetoothEnabled())
        showDialog = true

    if (showDialog) {
        SimpleConfirmationDialog(
            onConfirmation = {
                showDialog = false
            },
            dialogTitle = "Enable Bluetooth",
            dialogText = "This app uses Bluetooth, so please enable it!",
            icon = Icons.Default.Settings
        )
    }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = {
                clearResults()
                bluetoothScanner.startScan()
                enableRefresh = true

                CoroutineScope(Dispatchers.Default).launch {
                    delay(7000) // Stop scan after 7 seconds
                    bluetoothScanner.stopScan()

                    enableRefresh = false
                }
            },
            enabled = !enableRefresh
        ) {
            Text(text = "Rescan")
        }
        if (enableRefresh)
            CircularProgressIndicator(
                modifier = Modifier.width(24.dp)
            )
    }

    LazyColumn {
        items(bluetoothResults.toList()) { device ->
            val name = deviceNames[device.address]

            if (device.type == 2) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 4.dp),
                    onClick = { navController.navigate("device/${device.address}") }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp, horizontal = 6.dp)
                    ) {
                        Text(text = "Address:")
                        device.address?.let { address ->
                            Text(text = address)
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Name: ")
                            name?.let { deviceName ->
                                Text(text = deviceName)
                            }
                        }
                        Text(text = "Type: ${device.type}")
                    }
                }
            }
        }
    }

    LaunchedEffect(bluetoothResults) {
        val names = mutableMapOf<String, String>()
        bluetoothResults.forEach { device ->
            val name = device.name ?: "Unknown"
            names[device.address] = name
        }
        deviceNames = names
    }
}