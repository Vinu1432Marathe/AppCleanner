package com.software.app.update.smart.Activity

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import com.addsdemo.mysdk.ADPrefrences.MyApp
import com.addsdemo.mysdk.ADPrefrences.NativeAds_Class
import com.software.app.update.smart.Other.CircularProgressView
import com.software.app.update.smart.R

class BatteryInfoActivity : BaseActivity() {

    private lateinit var txtCapacity: TextView
    private lateinit var txtHealth: TextView
    private lateinit var txtCharging: TextView
    private lateinit var txtVoltage: TextView
    private lateinit var txtTechnology: TextView
    private lateinit var txtTemperature: TextView
    private lateinit var progressView: CircularProgressView
    private lateinit var container: LinearLayout
    private lateinit var imgBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_battery_info)

//        batteryInfoText = findViewById(R.id.batteryInfoText)

        txtCapacity  = findViewById(R.id.txtCapacity)
        txtHealth  = findViewById(R.id.txtHealth)
        txtCharging  = findViewById(R.id.txtCharging)
        txtVoltage  = findViewById(R.id.txtVoltage)
        txtTechnology  = findViewById(R.id.txtTechnology)
        txtTemperature  = findViewById(R.id.txtTemperature)
        imgBack  = findViewById(R.id.imgBack)

        progressView = findViewById<CircularProgressView>(R.id.circularProgress)
        progressView.showSubText = true  // Show text

        imgBack.setOnClickListener { onBackPressed() }
        displayBatteryInfo()

        container = findViewById(R.id.container)
        container.isVisible =
            MyApp.ad_preferences.getRemoteConfig()?.isAdShow == true

        val llline_full = findViewById<LinearLayout>(com.addsdemo.mysdk.R.id.llline_full)
        val llnative_full = findViewById<LinearLayout>(com.addsdemo.mysdk.R.id.llnative_full)
        NativeAds_Class.Fix_NativeFull_Show(this, llnative_full, llline_full, "medium")

    }

    private fun displayBatteryInfo() {
        val intent = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val batteryManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager

        val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val percentage = (level * 100) / scale

        val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL

        val plugged = intent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
        val chargingMethod = when (plugged) {
            BatteryManager.BATTERY_PLUGGED_USB -> "USB"
            BatteryManager.BATTERY_PLUGGED_AC -> "AC Adapter"
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless"
            else -> "Not Charging"
        }

        val health = when (intent?.getIntExtra(BatteryManager.EXTRA_HEALTH, 0)) {
            BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
            BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
            BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Failure"
            else -> "Unknown"
        }

        val temperature = (intent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)?.toFloat() ?: 0f) / 10
        val voltage = (intent?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)?.toFloat() ?: 0f) / 1000
        val technology = intent?.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "Unknown"
        val present = intent?.getBooleanExtra(BatteryManager.EXTRA_PRESENT, true) ?: true

        val capacity = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val capacityMicroAh = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
            val capacityMah = capacityMicroAh / 1000
            "$capacityMah mAh"
        } else {
            "Unavailable"
        }
        progressView.setProgressAnimated(percentage)
        txtCapacity.text = capacity
        txtHealth.text = health
        txtCharging.text = chargingMethod
        txtVoltage.text = buildString {
            append(voltage)
            append(" mV")
        }
        txtTechnology.text = technology
        txtTemperature.text = buildString {
            append(temperature)
            append(" °C")
        }
        if (isCharging){
            progressView.setSubText(getString(R.string.charging))
        }else{
            progressView.setSubText(getString(R.string.battery))
        }

        val batteryInfo = """
            🔋 Battery Level: $percentage%
            ⚡ Charging: ${if (isCharging) "Yes" else "No"}
            🔌 Charging Type: $chargingMethod
            ❤️ Health: $health
            🔥 Temperature: $temperature 
            ⚡ Voltage: $voltage V
            🧪 Technology: $technology
            🟢 Battery Present: $present
            📦 Capacity: $capacity
        """.trimIndent()

//        batteryInfoText.text = batteryInfo
    }
}

