package com.example.simulatorservice

import android.app.Service
import android.content.Intent
import android.os.Bundle
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
import java.util.Locale

class SimulatorService : Service() {

    private var TAG = "prudvi"

    val speedLive = MutableLiveData<Float>()

    var mySpeed : Float = 0.0f
    var myDistance : Float = 0.0f
    var myTime : String =""
    val distanceLive = MutableLiveData<Float>()
    val timeLive = MutableLiveData<String>()
    val modeChange  = MutableLiveData<String>()
    val ignState   = MutableLiveData<String>()
    val unitLive = MutableLiveData<String>()


    private var job : Job? = null
    private var socket : Socket? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG,"Service created")
        startSocketListener()
    }

    fun startSocketListener(){
        Log.d(TAG,"INISIDE startScoketListner()")
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
                         //   val unit = json.getString("unit")


                            mySpeed = speed
                            myDistance = distance
                            myTime = time
                            modeChange.postValue(mode)
                            ignState.postValue(ign)
                            //unitLive.postValue(unit)

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



    private val binder: ISimulatorInterface.Stub = object : ISimulatorInterface.Stub() {
        override fun getData(data: Bundle?) {
            Log.d(TAG,"getData() in service")
            val bundle = Bundle()
            bundle.putFloat("speed", speedLive.value ?: 0f)
            bundle.putFloat("distance", distanceLive.value ?: 0f)
            bundle.putLong("systemTime", timeLive.value?.toLong() ?: 0L)
            bundle.putBoolean("ignitionState", ignState.value?.lowercase(Locale.ROOT) == "on")
            bundle.putBoolean("mode", modeChange.value?.lowercase(Locale.ROOT) == "day")
          //  bundle.putBoolean("unit", unitLive.value?.lowercase(Locale.ROOT) == "km/h")
            Log.d(TAG,"Bundle data is ${bundle.toString()}")

            data?.putAll(bundle)
        }

        override fun getSpeed(): Float {
            return mySpeed
        }

        override fun getDistance(): Float {
            return myDistance
        }

        override fun getTime(): String? {
            return myTime
        }
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d(TAG,"onBind()")
        return binder
    }
}