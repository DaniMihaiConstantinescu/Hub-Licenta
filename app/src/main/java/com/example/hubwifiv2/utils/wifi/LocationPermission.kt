package com.example.hubwifiv2.utils.wifi

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat

class LocationPermission(
    private val activity: ComponentActivity
) {


    private val requestPermissionLauncher = activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            // Permission is granted, proceed with Wi-Fi scanning
//            registerWifiScanReceiver()
//            startWifiScan()
        } else {
            // Permission denied, handle accordingly
            // For example, show a message to the user indicating that the app cannot proceed without the permission
        }
    }

    fun checkAndRequestLocationPermission() {
        if (hasLocationPermission()) {
            // Permission is granted, proceed with Wi-Fi scanning
//            registerWifiScanReceiver()
//            startWifiScan()
        } else {
            // Permission is not granted, request it
            requestLocationPermission()
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

//    private fun registerWifiScanReceiver() {
//        val wifiManager = activity.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
//        val wifiScanReceiver = object : BroadcastReceiver() {
//            @SuppressLint("MissingPermission")
//            override fun onReceive(context: Context, intent: Intent) {
//                val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
//                if (success) {
//                    changeLoading(false)
//                    updateResults(wifiManager.scanResults)
//                } else {
//                    Log.e("WIFI RECEIVE ERROR", "Error in receiving Wi-Fi scan results")
//                    changeLoading(false)
//                }
//            }
//        }
//        val intentFilter = IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
//        activity.applicationContext.registerReceiver(wifiScanReceiver, intentFilter)
//    }
//
//    private fun startWifiScan() {
//        val wifiManager = activity.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
//        changeLoading(true)
//        val success = wifiManager.startScan()
//        if (!success) {
//            Log.e("WIFI RECEIVE ERROR", "Error starting Wi-Fi scan")
//            changeLoading(false)
//        }
//    }
}
