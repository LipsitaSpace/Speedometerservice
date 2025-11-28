package com.example.simulatorservice

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.Socket

class SimulatorService : Service() {

    private var TAG = "Service"

    val speedLive = MutableLiveData<Float>()
    val distanceLive = MutableLiveData<Float>()
    val timeLive = MutableLiveData<String>()
    val modeChange  = MutableLiveData<String>()
    val ignState   = MutableLiveData<String>()

    private var job : Job? = null
    private var socket : Socket? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG,"Service created")
        startSocketListener()
    }

    fun startSocketListener(){
        job = CoroutineScope(Dispatchers.IO).launch {

            val host = "10.0.2.2"
            val port = 5000

            while (isActive){

                try{
                    Log.d(TAG,"connection")
                    socket = Socket(host,port)
                    val reader = BufferedReader(InputStreamReader(socket!!.getInputStream()))
                    var line:String?
                    while (reader.readLine(). also { line =  it }!= null){
                        Log.d(TAG,"value is $line")
                        try{
                            val json = org.json.JSONObject(line!!)
                            val speed = json.getDouble("speed").toFloat()
                            val distance = json.getDouble("distance").toFloat()
                            val time = json.getString("system_time")
                            val mode = json.getString("mode")
                            val ign = json.getString("ignition")


                            speedLive.postValue(speed)
                            distanceLive.postValue(distance)
                            timeLive.postValue(time)
                            modeChange.postValue(mode)
                        } catch(e : Exception){
                            Log.e(TAG,"JSON parse error $e")
                        }
                    }
                } catch (e: Exception){
                    Log.e(TAG,"Exception is thrown $e")
                    delay(2000)
                }
            }
        }
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
}