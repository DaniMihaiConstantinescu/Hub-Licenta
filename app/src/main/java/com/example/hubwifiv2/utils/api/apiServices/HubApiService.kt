package com.example.hubwifiv2.utils.api.apiServices

import com.example.hubwifiv2.utils.dataClasses.devices.GeneralDevice
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

private const val mainResource ="hubs"
interface HubApiService {
    @POST("/$mainResource/add-device/{userId}/{hubId}")
    suspend fun addDeviceToHub(
        @Path("userId") userId: String,
        @Path("hubId") hubId: String,
        @Body newDevice:GeneralDevice
    ): Response<Void>
    @DELETE("/$mainResource/{userId}/{hubId}/{macAddress}")
    suspend fun deleteDeviceFromHub(
        @Path("userId") userId: String,
        @Path("hubId") sceneId: String,
        @Path ("macAddress") macAddress: String
    ): Response<Void>
}