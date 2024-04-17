package com.example.hubwifiv2

import android.content.Context
import android.net.wifi.ScanResult
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hubwifiv2.ui.theme.HubWifiV2Theme
import com.example.hubwifiv2.utils.WifiHandler
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

    private lateinit var wifiHandler: WifiHandler
    private var wifiResults by mutableStateOf<List<ScanResult>>(emptyList())
    private var isLoading by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // initialize tcp server and wifi handler
        tcpClient = TCPClient("192.168.1.100", 9090)
        GlobalScope.launch {
            tcpClient.connectToServer()
            sendInitializeMessageTCP(tcpClient, getAndroidId(applicationContext))
        }
        wifiHandler = WifiHandler(
            this,
            updateResults = { wifiResults = it },
            changeLoading = { isLoading = it }
        )

        setContent {
            HubWifiV2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    Column(modifier = Modifier.fillMaxSize()) {
                        TCPTest(tcpClient, applicationContext)
                        WiFis(wifiResults, isLoading)

                        DevicesButtons(applicationContext)
                    }

                }
            }
        }

        // Check and request location permission
        wifiHandler.checkAndRequestLocationPermission()
    }

    override fun onDestroy() {
        super.onDestroy()
        tcpClient.disconnect()
    }

}


@Composable
fun WiFis(wifiScanResults: List<ScanResult>, isLoading: Boolean) {
    if (isLoading) {
        Text(text = "Loading...")
    } else {
        // Display the list of Wi-Fi scan results
        LazyColumn{
            items(wifiScanResults){ wifi ->
                Text(text = wifi.SSID)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TCPTest(
    tcpClient: TCPClient,
    context: Context
){

    var text by remember {
        mutableStateOf("")
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 50.dp)
    ) {
        TextField(value = text, onValueChange = {text = it})
        Button(onClick = {
            val macAddr = getAndroidId(context)
            sendMessageTCP(tcpClient, macAddr, "1.1.1.1", mapOf("temp" to "20"))
        }) {
            Text(text = "Send")
        }
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