package com.example.hubwifiv2.utils.csv

import android.content.Context
import android.util.Log
import com.example.hubwifiv2.utils.dataClasses.devices.GeneralDevice
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.lang.Exception

class CSVUtils(private val context: Context) {

    private val fileName = "device_data.csv"

    fun readDataFromCSV(): List<GeneralDevice> {
        val deviceList = mutableListOf<GeneralDevice>()
        try {
            val file = File(context.filesDir, fileName)
            if (!file.exists()) {
                return deviceList
            }
            BufferedReader(FileReader(file)).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    val tokens = line?.split(",")
                    if (tokens?.size ?: 0 >= 4) {
                        val device = GeneralDevice(
                            tokens?.getOrNull(0) ?: "",
                            tokens?.getOrNull(1) ?: "",
                            tokens?.getOrNull(2) ?: "",
                            tokens?.getOrNull(3) ?: ""
                        )
                        deviceList.add(device)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("CSVUtils", "Error reading CSV: ${e.message}")
        }
        return deviceList
    }

    fun writeDataToCSV(deviceList: List<GeneralDevice>) {
        try {
            val file = File(context.filesDir, fileName)
            FileWriter(file).use { writer ->
                deviceList.forEach { device ->
                    writer.append("${device.deviceMAC ?: ""},${device.hubMac ?: ""},${device.name ?: ""},${device.type ?: ""}\n")
                }
            }
        } catch (e: Exception) {
            Log.e("CSVUtils", "Error writing CSV: ${e.message}")
        }
    }

    fun addRowToCSV(device: GeneralDevice) {
        try {
            val file = File(context.filesDir, fileName)
            FileWriter(file, true).use { writer ->
                writer.append("${device.deviceMAC ?: ""},${device.hubMac ?: ""},${device.name ?: ""},${device.type ?: ""}\n")
            }
        } catch (e: Exception) {
            Log.e("CSVUtils", "Error adding row to CSV: ${e.message}")
        }
    }

    fun removeRowFromCSV(deviceMAC: String) {
        try {
            val file = File(context.filesDir, fileName)
            val tempList = mutableListOf<GeneralDevice>()
            BufferedReader(FileReader(file)).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    val tokens = line?.split(",")
                    if (tokens?.size ?: 0 >= 4 && tokens?.getOrNull(0) != deviceMAC) {
                        tempList.add(
                            GeneralDevice(
                                tokens?.getOrNull(0) ?: "",
                                tokens?.getOrNull(1) ?: "",
                                tokens?.getOrNull(2) ?: "",
                                tokens?.getOrNull(3) ?: ""
                            )
                        )
                    }
                }
            }
            writeDataToCSV(tempList)
        } catch (e: Exception) {
            Log.e("CSVUtils", "Error removing row from CSV: ${e.message}")
        }
    }
}
