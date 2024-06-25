package com.example.hubwifiv2.utils.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hubwifiv2.utils.api.RetrofitClient
import com.example.hubwifiv2.utils.dataClasses.devices.GeneralDevice
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import retrofit2.HttpException

class HubViewModel :ViewModel(){

    val auth = Firebase.auth
    private var userId by mutableStateOf("")

    init {
        auth.currentUser?.run {
            userId = uid
        }
    }

    fun addDeviceToHub(hubMac: String, newDevice: GeneralDevice) {
        viewModelScope.launch {
            try {
                // Make the API call to add the device to the room
                val response = RetrofitClient.hubServices.addDeviceToHub(userId, hubMac, newDevice)
                if (response.isSuccessful) {

                } else {
                    Log.e("ADD DEVICE ERROR", "HTTP Error: ${response.code()}")
                }
            } catch (e: HttpException) {
                if (e.code() == 404) {
                    Log.e("ADD DEVICE ERROR", "Device not found")
                } else {
                    Log.e("ADD DEVICE ERROR", "HTTP Error")
                }
            } catch (e: Exception) {
                Log.e("ADD DEVICE ERROR", "Server Error")
            }
        }
    }
    fun deleteDeviceFromHub(hubMac: String, deviceMac: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.hubServices.deleteDeviceFromHub("1", hubMac, deviceMac)
                if (response.isSuccessful) {

                } else {
                    Log.e("DELETE DEVICE ERROR", "HTTP Error: ${response.code()}")
                }
            } catch (e: HttpException) {
                if (e.code() == 404) {
                    Log.e("DELETE DEVICE ERROR", "Device not found")
                } else {
                    Log.e("DELETE DEVICE ERROR", "HTTP Error")
                }
            } catch (e: Exception) {
                Log.e("DELETE DEVICE ERROR", "Server Error")
            }
        }
    }

}