package com.example.hubwifiv2.utils.wifi

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import android.util.Log


fun connectToHotspot(context: Context, ssid: String, password: String) {

    val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        // For Android versions below 10 (API level 29)
        val wifiConfig = WifiConfiguration()
        wifiConfig.SSID = "\"" + ssid + "\""
        wifiConfig.preSharedKey = "\"" + password + "\""

        val netId = wifiManager.addNetwork(wifiConfig)
        wifiManager.disconnect()
        wifiManager.enableNetwork(netId, false)
    } else {
        // For Android versions 10 and above (API level 29)
        val networkSpecifier = WifiNetworkSpecifier.Builder()
            .setSsid(ssid)
            .setWpa2Passphrase(password)
            .build()

        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()

        val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
        connectivityManager.requestNetwork(networkRequest, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                // Connection successful
                Log.e("WIFI", "Connected succesfully")

            }

            override fun onLost(network: Network) {
                super.onLost(network)
                // Connection lost
                Log.e("WIFI", "Connection lost")

            }
        })
    }
}


