package com.example.hubwifiv2.utils.tcp

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class TCPClient (private val serverIp: String, private val serverPort: Int) {

    private var socket: Socket? = null
    private var reader: BufferedReader? = null
    private var writer: PrintWriter? = null

    fun connectToServer() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                socket = Socket(serverIp, serverPort)
                Log.i("TCP CONNECT", "Connected to server: $serverIp:$serverPort")
                reader = BufferedReader(InputStreamReader(socket?.getInputStream()))
                writer = PrintWriter(socket?.getOutputStream(), true)

                // Continuously listen for incoming data
                listenForData()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("ERROR TCP", e.toString())
            }
        }
    }

    private fun listenForData() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val bufferedReader = BufferedReader(InputStreamReader(socket?.getInputStream()))
                val charBuffer = CharArray(4096)
                while (true) {
                    val bytesRead = bufferedReader.read(charBuffer)
                    if (bytesRead != -1) {
                        val receivedData = String(charBuffer, 0, bytesRead)
                        Log.e("TCP Listen", "Received data from server: $receivedData")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun sendDataToServer(data: String) {
        GlobalScope.launch(Dispatchers.IO) {
            writer?.println(data)
            writer?.flush()
        }
    }

    fun disconnect() {
        GlobalScope.launch(Dispatchers.IO) {
            socket?.close()
            reader?.close()
            writer?.close()
        }
    }
}
