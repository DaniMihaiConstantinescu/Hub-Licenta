package com.example.hubwifiv2.utils.tcp

import android.content.Context
import android.util.Log
import com.example.hubwifiv2.utils.dataClasses.tcp.TCPMessage
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class TCPClient (appContext: Context ,private val serverIp: String, private val serverPort: Int) {

    private var socket: Socket? = null
    private var reader: BufferedReader? = null
    private var writer: PrintWriter? = null

    private val context = appContext

    fun connectToServer(after: () -> Unit = {} ) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                socket = Socket(serverIp, serverPort)
                Log.i("TCP", "Connected to server: $serverIp:$serverPort")
                reader = BufferedReader(InputStreamReader(socket?.getInputStream()))
                writer = PrintWriter(socket?.getOutputStream(), true)

                // Continuously listen for incoming data
                listenForData()
                after()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("TCP", e.toString())
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
                        Log.i("TCP", "Received data from server: $receivedData")

                        // parse the data and forward it to the device
                        val gson = Gson()
                        val tcpMessage = gson.fromJson(receivedData, TCPMessage::class.java)
                        GlobalScope.launch {
                            try {
                                forwardMessageToDevice(context, tcpMessage)
                            } catch (e: Exception) {
                                Log.e("TCP", "Error forwarding message to device: ${e.message}")
                            }
                        }
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
