package com.ebookfrenzy.emisorbeacon

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.Context
import android.os.ParcelUuid
import android.util.Log

class BeaconEmitter (context: Context) {


    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter:BluetoothAdapter? = bluetoothManager.adapter
    private val advertiser: BluetoothLeAdvertiser?=bluetoothAdapter?.bluetoothLeAdvertiser

    fun startAdvertising(){

        try {
            if(advertiser ==null){
                Log.e("BeaconEmitter", "Failed to create advertiser")
                return
            }

            val settings = AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                .setConnectable(false)
                .build()

            val data = AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .addServiceUuid(ParcelUuid.fromString(SERVICE_UUID))
                .build()

            advertiser.startAdvertising(settings, data, advertiseCallback)
        }catch (e: SecurityException){
            Log.e("BeaconEmitter","SecurityException: ${e.message}")
        }


    }

    fun stopAdvertising() {
        try {
            advertiser?.stopAdvertising(advertiseCallback)
        } catch (e: SecurityException) {
            Log.e("BeaconEmitter", "SecurityException: ${e.message}")
        }
    }

    private val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
            Log.i("BeaconEmitter", "Advertising started successfully")
        }

        override fun onStartFailure(errorCode: Int) {
            Log.e("BeaconEmitter", "Advertising failed with error code: $errorCode")
        }
    }


    companion object {
        private const val SERVICE_UUID = "0000180D-0000-1000-8000-00805F9B34FB" // UUID para servicios genéricos, puedes cambiarlo
    }
}