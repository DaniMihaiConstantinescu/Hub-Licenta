package com.example.hubwifiv2

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hubwifiv2.ui.homepage.HomeScreen
import com.example.hubwifiv2.ui.homepage.devices.AllDevicesScreen
import com.example.hubwifiv2.ui.homepage.devices.DeviceScreen
import com.example.hubwifiv2.ui.theme.HubWifiV2Theme
import com.example.hubwifiv2.utils.ble.BluetoothScanner
import com.example.hubwifiv2.utils.permissionHandling.LocationPermission
import com.example.hubwifiv2.utils.tcp.TCPClient
import com.example.hubwifiv2.utils.tcp.getAndroidId
import com.example.hubwifiv2.utils.tcp.sendInitializeMessageTCP
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var tcpClient: TCPClient

    private val REQUEST_CODE_BLUETOOTH  = 101
    private val bluetoothResults = mutableSetOf<BluetoothDevice>()
    private lateinit var bluetoothScanner: BluetoothScanner

    private lateinit var locationPermission: LocationPermission

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationPermission = LocationPermission(this)

        // initialize tcp server and bluetooth handler
        tcpClient = TCPClient(applicationContext ,applicationContext.getString(R.string.server_ip), 9090)

        GlobalScope.launch {
            tcpClient.connectToServer(after = {
                sendInitializeMessageTCP(tcpClient, getAndroidId(applicationContext))
            })
        }
        
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
                                AllDevicesScreen(navController)
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
        requestBluetoothPermission()
    }
    override fun onPause() {
        super.onPause()
        bluetoothScanner.stopScan()
    }
    override fun onDestroy() {
        super.onDestroy()
        bluetoothScanner.stopScan()
        tcpClient.disconnect()
    }

}
