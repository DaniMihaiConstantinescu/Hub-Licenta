package com.example.hubwifiv2.ui.homepage.devices

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hubwifiv2.utils.ble.BluetoothManager
import com.example.hubwifiv2.utils.csv.CSVUtils
import com.example.hubwifiv2.utils.dataClasses.devices.GeneralDevice
import com.example.hubwifiv2.utils.tcp.getAndroidId
import com.example.hubwifiv2.utils.viewModels.HubViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun DeviceScreen(
    address: String,
    navController: NavController
){
    var startedConnect by remember { mutableStateOf(false) }
    var isConnecting by remember { mutableStateOf(false) }
    var deviceType by remember { mutableStateOf("") }
    var deviceName by remember { mutableStateOf("") }
    
    val context = LocalContext.current
    val bluetoothManager = BluetoothManager(context, true, setType = {type ->
        isConnecting = false
        deviceType = type
    })
    val deviceViewModel = viewModel<HubViewModel>()

    Column(
        Modifier.padding(vertical = 12.dp, horizontal = 20.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ){
            Text(text = "Device address: $address")
        }

        if (!startedConnect || isConnecting) {
            Spacer(modifier = Modifier.height(36.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ){
                Button(
                    onClick = {
                        bluetoothManager.stopScan()
                        bluetoothManager.disconnect()
                        navController.navigate("home")
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Text(text = "Cancel", color = Color.White)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    enabled = !isConnecting,
                    shape = RoundedCornerShape(12.dp),
                    onClick = {
                        bluetoothManager.scanForDevice(address)
                        startedConnect = true
                        isConnecting = true
                    }
                ) {
                    Text(text = "Connect")
                }
            }
        }

        if (startedConnect) {
            if (isConnecting) {
                Spacer(modifier = Modifier.height(36.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(6.dp))

                    if (deviceName == "")
                        Text(text = "Bluetooth name: Unknown")
                    else
                        Text(text = "Bluetooth name: $deviceName")

                    Text(text = "Type: $deviceType")

                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Device name:")
                        Spacer(modifier = Modifier.width(4.dp))
                        TextField(value = deviceName, onValueChange = {deviceName = it})
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = {
                                bluetoothManager.disconnect()
                                navController.navigate("home")
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                        ) {
                            Text(text = "Cancel", color = Color.White)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = {
                                if (deviceName == "") {
                                    Toast.makeText(
                                        context,
                                        "The name of the device needs to be completed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    addDevice(
                                        context,
                                        navController,
                                        deviceViewModel,
                                        address,
                                        deviceName,
                                        deviceType
                                    )
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Text(text = "Add Device")
                        }
                    }
                }
            }
        }
    }

}

fun addDevice(
    context:Context,
    navController: NavController,
    deviceViewModel: HubViewModel,
    address: String,
    deviceName: String,
    deviceType: String
) {
    // add to csv and to db
    val csvUtils = CSVUtils(context)
    val hubID = getAndroidId(context)

    val newDevice = GeneralDevice(
        deviceMAC =  address,
        hubMac = hubID,
        name =  deviceName,
        type =  deviceType
    )
    csvUtils.addDevice(newDevice)
    deviceViewModel.addDeviceToHub(hubID, newDevice)

    navController.navigate("home")
}