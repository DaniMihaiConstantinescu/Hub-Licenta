package com.example.hubwifiv2.ui.homepage.wifi

import android.net.wifi.ScanResult
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun WiFiList(
    wifiScanResults: List<ScanResult>,
    isLoading: Boolean,
    navController: NavController
) {

    val w = listOf("WiFi1", "WiFi2", "WiFi 3", "WiFi4")
    LazyColumn{
        items(w){ wifi ->
            WifiCard(wifi, navController)
        }
    }

//    if (isLoading) {
//        Text(text = "Loading...")
//    } else {
//        LazyColumn{
//            items(wifiScanResults){ wifi ->
//                WifiCard(wifi.SSID)
//            }
//        }
//    }
}

@Composable
fun WifiCard(
    wifiName: String,
    navController: NavController
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { navController.navigate("wifi/${wifiName}") }
    ) {
        Text(
            text = wifiName,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            fontSize = 18.sp
        )
    }
}