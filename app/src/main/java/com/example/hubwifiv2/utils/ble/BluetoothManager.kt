package com.example.hubwifiv2.utils.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.util.Log
import java.util.UUID

class BluetoothManager(
    context: Context,
    setType: (type: String) -> Unit
) {
    companion object {
        private const val TAG = "BluetoothManager"
        private val SERVICE_UUID = UUID.fromString("4fafc201-1fb5-459e-8fcc-c5c9c331914b")
        private val CHARACTERISTIC_UUID = UUID.fromString("beb5483e-36e1-4688-b7f5-ea07361b26a8")
        private val CHARACTERISTIC_WRITE_UUID = UUID.fromString("3b486277-d8fe-4757-96ec-b465c0aca0f5")
    }

    private var bluetoothGatt: BluetoothGatt? = null

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val gattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    Log.d(TAG, "Connected to GATT server.")
                    bluetoothGatt = gatt
                    gatt?.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.d(TAG, "Disconnected from GATT server.")
                }
            }
        }

        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // get characteristic and set notifications on
                val characteristic = getCharacteristic(gatt, SERVICE_UUID, CHARACTERISTIC_UUID)
                gatt?.setCharacteristicNotification(characteristic, true)

                // send mess to get initial type
                sendMessage("0")

            } else {
                Log.e(TAG, "onServicesDiscovered received: $status")
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
            super.onCharacteristicChanged(gatt, characteristic)
            characteristic?.let {
                val message = String(it.value)
                Log.i(TAG, "Message received from BLE device: $message")

                // verify if it is init message
                val type = BLEUtils.extractType(message)
                if ( type != null ){
                    setType(type)
                }
            }
        }

    }

    @SuppressLint("MissingPermission")
    fun sendMessage(message: String) {
        if (bluetoothGatt != null) {
            val writeCharacteristic = getCharacteristic(bluetoothGatt, SERVICE_UUID, CHARACTERISTIC_WRITE_UUID)
            writeCharacteristic?.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            writeCharacteristic?.value = message.toByteArray()
            bluetoothGatt?.writeCharacteristic(writeCharacteristic)
        } else {
            Log.e(TAG, "BluetoothGatt is not available. Ensure device is connected.")
        }
    }

    private fun getCharacteristic(gatt: BluetoothGatt?, serviceUuid: UUID, characteristicUuid: UUID): BluetoothGattCharacteristic? {
        val service: BluetoothGattService? = gatt?.getService(serviceUuid)
        return service?.getCharacteristic(characteristicUuid)
    }

    @SuppressLint("MissingPermission")
    fun connectToDevice(device: BluetoothDevice, context: Context) {
        device.connectGatt( context, false, gattCallback)
    }

    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            stopScan()
            connectToDevice(result.device, context)
        }
    }
    @SuppressLint("MissingPermission")
    fun scanForDevice(address: String) {
        val filter = ScanFilter.Builder().setDeviceAddress(address).build()
        val scanSettings = ScanSettings.Builder().build()

        bluetoothAdapter?.bluetoothLeScanner?.startScan(listOf(filter), scanSettings, scanCallback)
    }

    @SuppressLint("MissingPermission")
    fun stopScan() {
        bluetoothAdapter?.bluetoothLeScanner?.stopScan(scanCallback)
    }
}

