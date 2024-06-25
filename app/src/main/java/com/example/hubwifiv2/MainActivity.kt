package com.example.hubwifiv2

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hubwifiv2.ui.auth.SignInScreen
import com.example.hubwifiv2.ui.homepage.HomeScreen
import com.example.hubwifiv2.ui.homepage.devices.AllDevicesScreen
import com.example.hubwifiv2.ui.homepage.devices.DeviceScreen
import com.example.hubwifiv2.ui.theme.HubWifiV2Theme
import com.example.hubwifiv2.utils.auth.SignInViewModel
import com.example.hubwifiv2.utils.auth.logic.GoogleUiClient
import com.example.hubwifiv2.utils.ble.BluetoothScanner
import com.example.hubwifiv2.utils.permissionHandling.LocationPermission
import com.example.hubwifiv2.utils.tcp.TCPClient
import com.example.hubwifiv2.utils.tcp.getAndroidId
import com.example.hubwifiv2.utils.tcp.sendInitializeMessageTCP
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val googleAuthUiClient by lazy {
        GoogleUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

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
                            startDestination = "sign_in"
                        ){
                            composable("sign_in") {
                                val viewModel = viewModel<SignInViewModel>()
                                val state by viewModel.state.collectAsStateWithLifecycle()

                                val launcher = rememberLauncherForActivityResult(
                                    contract = ActivityResultContracts.StartIntentSenderForResult(),
                                    onResult = {result ->
                                        if (result.resultCode == RESULT_OK) {
                                            lifecycleScope.launch {
                                                val signInResult = googleAuthUiClient.signInWithIntent(
                                                    intent = result.data ?: return@launch
                                                )
                                                viewModel.onSingInResult(signInResult)
                                            }
                                        }
                                    }
                                )

                                LaunchedEffect(key1 = state.isSignInSuccessful) {
                                    if (state.isSignInSuccessful) {
                                        navController.navigate("home")
                                        viewModel.resetState()
                                    }
                                }

                                SignInScreen(
                                    state = state,
                                    onSignInClick = {
                                        lifecycleScope.launch {
                                            val signInIntentSender = googleAuthUiClient.signIn()
                                            launcher.launch(
                                                IntentSenderRequest.Builder(
                                                    signInIntentSender ?: return@launch
                                                ).build()
                                            )
                                        }
                                    }
                                )
                            }
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
