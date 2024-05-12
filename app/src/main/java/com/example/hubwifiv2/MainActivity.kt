package com.example.hubwifiv2

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hubwifiv2.ui.homepage.HomeScreen
import com.example.hubwifiv2.ui.homepage.devices.AllDevicesScreen
import com.example.hubwifiv2.ui.homepage.devices.DeviceScreen
import com.example.hubwifiv2.ui.theme.HubWifiV2Theme
import com.example.hubwifiv2.utils.ble.BluetoothScanner
import com.example.hubwifiv2.utils.wifi.LocationPermission
import com.example.hubwifiv2.utils.dataClasses.devices.GeneralDevice
import com.example.hubwifiv2.utils.tcp.TCPClient
import com.example.hubwifiv2.utils.tcp.getAndroidId
import com.example.hubwifiv2.utils.tcp.sendInitializeMessageTCP
import com.example.hubwifiv2.utils.tcp.sendMessageTCP
import com.example.hubwifiv2.utils.viewModels.HubViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var tcpClient: TCPClient

    private lateinit var locationPermission: LocationPermission

    private val REQUEST_CODE_BLUETOOTH  = 101
    private val bluetoothResults = mutableSetOf<BluetoothDevice>()
    private lateinit var bluetoothScanner: BluetoothScanner

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // initialize tcp server and wifi handler
        tcpClient = TCPClient("192.168.1.103", 9090)
        GlobalScope.launch {
            tcpClient.connectToServer()
            sendInitializeMessageTCP(tcpClient, getAndroidId(applicationContext))
        }

        locationPermission = LocationPermission(this)

        bluetoothScanner = BluetoothScanner(this)
        bluetoothScanner.onDeviceFound = { device ->
            bluetoothResults.add(device)
        }

        setContent {
            HubWifiV2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold {
                        val navController = rememberNavController()
                        NavHost(
                            navController = navController,
                            startDestination = "home"
                        ){
                            composable("home"){
                                HomeScreen(
                                    tcpClient,
                                    applicationContext,
                                    navController,
                                    bluetoothScanner,
                                    bluetoothResults,
                                    clearResults = {
                                        bluetoothResults.clear()
                                    }
                                )
                            }
                            composable("device/{addr}"){backStackEntry ->
                                val addr = backStackEntry.arguments?.getString("addr")
                                addr?.let {
                                    DeviceScreen(addr, navController)
                                }
                            }
                            composable("all-devices"){
                                AllDevicesScreen()
                            }
                        }
                    }
                }
            }
        }

        // Check and request location && bluetooth permissions
        locationPermission.checkAndRequestLocationPermission()
        requestBluetoothPermission()
    }

    private fun checkBluetoothEnabled() {
        if (bluetoothScanner.isBluetoothEnabled()) {
            // Bluetooth is enabled, start scanning
            bluetoothScanner.startScan()
        } else {
        }
    }

    private fun requestBluetoothPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ) {
            ActivityCompat.requestPermissions(this, arrayOf(
                android.Manifest.permission.BLUETOOTH,
                android.Manifest.permission.BLUETOOTH_SCAN,
                android.Manifest.permission.BLUETOOTH_CONNECT,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), REQUEST_CODE_BLUETOOTH)
        } else {
            checkBluetoothEnabled()
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_BLUETOOTH) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // bluetoothScanner.startScan()
            } else {
                // Handle permission denied case (optional)
            }
        }
    }
    override fun onResume() {
        super.onResume()
        requestBluetoothPermission() // Request permission if needed
    }
    override fun onPause() {
        super.onPause()
        bluetoothScanner.stopScan() // Stop scanning when activity pauses
    }
    override fun onDestroy() {
        super.onDestroy()
        bluetoothScanner.stopScan()
        tcpClient.disconnect()
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TCPTest(
    tcpClient: TCPClient,
    context: Context
){

    var text by remember { mutableStateOf("") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 50.dp)
    ) {
        Button(onClick = {
            val macAddr = getAndroidId(context)
            sendMessageTCP(tcpClient, macAddr, "1.1.1.1", mapOf("temp" to "20"))
        }) {
            Text(text = "Send")
        }
        TextField(value = text, onValueChange = {text = it})
    }

}

@Composable
fun DevicesButtons(context: Context){

    val deviceViewModel = viewModel<HubViewModel>()
    val hubId = getAndroidId(context)

    Row {
        Button(onClick = {
            deviceViewModel.addDeviceToHub(
                hubId,
                GeneralDevice(
                    deviceMAC = "mac1",
                    hubMac = hubId,
                    type = "ac",
                    name = "Device 1"
                )
            )
        }) {
            Text(text = "Add")
        }


        Button(onClick = {
            deviceViewModel.deleteDeviceFromHub(
                hubId,
                "mac1"
            )
        }) {
            Text(text = "Remove")
        }
    }
}