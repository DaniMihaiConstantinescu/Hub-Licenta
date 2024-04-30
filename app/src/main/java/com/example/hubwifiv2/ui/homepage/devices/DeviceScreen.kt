package com.example.hubwifiv2.ui.homepage.devices

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.hubwifiv2.utils.wifi.connectToHotspot

@Composable
fun DeviceScreen(
    SSID: String
){

    Text(text = SSID)
    
//    var showPass by remember { mutableStateOf(true) }
//
//    Column(
//        modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)
//    ) {
//        Text(
//            text = SSID,
//            modifier = Modifier.fillMaxWidth(),
//            textAlign = TextAlign.Center,
//            style = MaterialTheme.typography.headlineMedium
//        )
//
//        if (showPass) Password(SSID)
//    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Password(SSID: String){
    var password by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp)
    ) {
        Text(
            text = "Password:",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        TextField(
            modifier = Modifier
                .clickable { }
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            value = password,
            onValueChange = { password = it },
            singleLine = true
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { /*TODO: try to connect to wifi*/
                    Log.e("WIFI", "Start try connect")
                    connectToHotspot(context, SSID, password)
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(
                    text = "Connect",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }
    }
}