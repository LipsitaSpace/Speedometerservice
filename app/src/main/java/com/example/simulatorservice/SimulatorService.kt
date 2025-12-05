package com.example.simulatorservice

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.Socket
import java.util.Locale

class SimulatorService : Service() {

    private var TAG = "SimulatorService"

    var serviceSpeed: Float = 0.0f
    var serviceDistance: Float = 0.0f
    var serviceTime: String = ""
    var serviceMode: String = ""
    var serviceUnit: String = "km/h"
    var serviceTotalDistance: Float = 0.0f
    var serviceTotalTime: Float = 0.0f


    private var job: Job? = null
    private var socket: Socket? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")
        startSocketListener()
    }

    fun startSocketListener() {
        Log.d(TAG, "INISIDE startScoketListner()")
        job = CoroutineScope(Dispatchers.IO).launch {

            val host = "192.168.70.212"
            val port = 5000

            while (isActive) {

                try {
                    Log.d(TAG, "connection")
                    socket = Socket(host, port)
                    val reader = BufferedReader(InputStreamReader(socket!!.getInputStream()))
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        Log.d(TAG, "value is $line")
                        try {
                            val json = org.json.JSONObject(line!!)
                            val jsonSpeed = json.getDouble("speed").toFloat()
                            val jsonDistance = json.getDouble("distance").toFloat()
                            val jsonTime = json.getString("system_time")
                            val jsonMode = json.getString("mode")
                            val jsonUnit = json.getString("unit")
                            val jsonTotalDistance = json.getDouble("total_distance").toFloat()
                            val jsonTotalTime = json.getDouble("total_time").toFloat()



                            serviceSpeed = jsonSpeed
                            serviceDistance = jsonDistance
                            serviceTime = jsonTime.substringAfter(" ")
                            serviceMode = jsonMode
                            serviceUnit = jsonUnit
                            serviceTotalDistance = jsonTotalDistance
                            serviceTotalTime = jsonTotalTime

                        } catch (e: Exception) {
                            Log.e(TAG, "JSON parse error $e")
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Exception is thrown $e")
                    delay(2000)
                }
            }
        }
    }

    private val binder: ISimulatorInterface.Stub = object : ISimulatorInterface.Stub() {

        override fun getSpeed(): Float {
            return serviceSpeed
        }

        override fun getDistance(): Float {
            return serviceDistance
        }

        override fun getTime(): String {
            return serviceTime
        }

        override fun getMode(): Boolean {
            return serviceMode.lowercase(Locale.ROOT) == "night"
        }

        override fun getUnit(): String {
            return serviceUnit
        }

        override fun getTotalDistance(): Float {
            return serviceTotalDistance
        }

        override fun getTotalTime(): Float {
            return serviceTotalTime
        }
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d(TAG, "onBind()")
        return binder
    }
}