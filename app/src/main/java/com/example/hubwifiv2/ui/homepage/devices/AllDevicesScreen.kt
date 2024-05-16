package com.example.hubwifiv2.ui.homepage.devices

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hubwifiv2.utils.csv.CSVUtils
import com.example.hubwifiv2.utils.dataClasses.devices.GeneralDevice
import com.example.hubwifiv2.utils.viewModels.HubViewModel

@Composable
fun AllDevicesScreen(navController: NavController){

    val context = LocalContext.current
    val csvUtils = CSVUtils(context)
    val deviceViewModel = viewModel<HubViewModel>()

    var devices by remember { mutableStateOf(csvUtils.readDataFromCSV()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "All Devices",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn{
            items(devices){device ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    IconButton(onClick = {
                        // remove from csv and db
                        devices = devices.filter { it.deviceMAC != device.deviceMAC }
                        deviceViewModel.deleteDeviceFromHub(device.hubMac, device.deviceMAC)
                        csvUtils.removeRowFromCSV(device.deviceMAC)

                    }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Device")
                    }
                    DeviceCard(device)
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Button(onClick = { navController.navigate("home") }) {
            Text(text = "Return to homepage")
        }
    }

}

@Composable
fun DeviceCard(device: GeneralDevice){
    Card {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = device.name, fontWeight = FontWeight.Bold)
                Text(text = device.type)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Address: ${device.deviceMAC}")
        }
    }
}