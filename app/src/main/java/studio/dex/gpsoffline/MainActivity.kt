package studio.dex.gpsoffline

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import studio.dex.gpsofflinelibrary.GPSConst
import studio.dex.gpsofflinelibrary.GPSService
class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        registerGPSService()
    }

    private fun registerGPSService() {

         registerReceiver(object :BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                val location = intent?.getParcelableExtra<Location>(GPSConst.GPS_RESULT_TAG)
               val locationStr = "long=${location?.longitude};lat=${location?.latitude}\n"
                findViewById<TextView>(R.id.tv_1).apply {
                    text = StringBuffer().append(this.text).append(locationStr)
                }
            }
        }, IntentFilter(GPSConst.GPS_RESULT_ACTION))
    }

    fun startService(view:View)
    {
        GPSService.startService(this)
    }
    fun stopService(view: View)
    {
        GPSService.stopService(this)
    }
}