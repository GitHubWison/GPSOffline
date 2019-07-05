package studio.dex.gpsofflinelibrary

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.widget.Toast
import android.location.Criteria
import android.os.Looper
import java.util.concurrent.Executors


class GPSConst {
    companion object {
        const val GPS_RESULT_ACTION = "dex.studio.gps.location"
        const val GPS_RESULT_TAG = "GPS_RESULT_TAG"
         const val UPDATE_FREQUENCY = 5_000L
    }
}

@SuppressLint("MissingPermission")
class GPSService : Service() {
    private var currentLocation: Location? = null
    private var thread: Thread? = null
    private var isServiceStop: Boolean = false
    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = object : LocationListener {
        override fun onLocationChanged(location: Location?) {
            currentLocation = location
        }
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        }

        override fun onProviderEnabled(provider: String?) {
        }

        override fun onProviderDisabled(provider: String?) {
        }
    }

    companion object {
        fun startService(context: Context) {
            context.startService(Intent(context, GPSService::class.java))
        }

        fun stopService(context: Context) {
            context.stopService(Intent(context, GPSService::class.java))
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        throw  UnsupportedOperationException("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        startGPSService()
        startSendGPSInfo()
    }

    //    回传gps信息
    private fun startSendGPSInfo() {
        if (thread == null) {
            thread = Thread(Runnable {
                Looper.prepare()
                while (!isServiceStop) {
                    Log.e("gps==", "上传..")
                    if (currentLocation != null) {
                        this@GPSService.sendBroadcast(Intent()
                            .apply {
                                action = GPSConst.GPS_RESULT_ACTION
                                putExtra(GPSConst.GPS_RESULT_TAG, currentLocation)
                            })
                    }

                    try {
                        Thread.sleep(GPSConst.UPDATE_FREQUENCY)
                    } catch (e: InterruptedException) {
                        Thread.interrupted()
                    }
                }
            })
        }
        thread?.start()
    }


    //监听gps回传的信息

    private fun startGPSService() {
        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPSConst.UPDATE_FREQUENCY, 0f, locationListener)
    }

    override fun onDestroy() {
        Log.e("gps==", "onDestroy")
//        将监听的线程删除
        thread?.interrupt()
        thread = null
        isServiceStop = true
//        关闭gps
        if (locationManager != null) {
            locationManager?.removeUpdates(locationListener)
            locationManager = null
        }
        if (locationListener != null) {
            locationListener = null
        }
        currentLocation = null
        super.onDestroy()

    }

}