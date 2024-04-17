package com.example.hubwifiv2.utils.api

import com.example.hubwifiv2.utils.api.apiServices.HubApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val currentIp = "192.168.1.100"
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