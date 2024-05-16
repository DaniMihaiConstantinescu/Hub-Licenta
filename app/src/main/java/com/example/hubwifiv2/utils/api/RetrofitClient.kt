package com.example.hubwifiv2.utils.api

import androidx.compose.ui.res.stringResource
import com.example.hubwifiv2.utils.api.apiServices.HubApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val currentIp = "192.168.65.40"
    private const val baseUrl = "http://$currentIp:5000"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val hubServices: HubApiService by lazy {
        retrofit.create(HubApiService::class.java)
    }

}