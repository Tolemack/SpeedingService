package com.example.speedingservice

import android.app.NotificationManager
import android.app.Service
import android.car.Car
import android.car.VehiclePropertyIds
import android.car.hardware.CarPropertyValue
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.car.app.notification.CarAppExtender
import androidx.car.app.notification.CarNotificationManager
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import android.car.hardware.property.CarPropertyManager

class SpeedingService : Service() {
    private val TAG = "SpeedingService"

    private lateinit var  mCar: Car
    private lateinit var mCarPropertyManager: CarPropertyManager

    private var NotifHunShowed = false

    private val speedingServiceBinder: IBinder = SpeedingServiceBinder()
    inner class SpeedingServiceBinder : Binder() {
        fun getService(): SpeedingService = this@SpeedingService
    }

    private var carPropertyListener = object : CarPropertyManager.CarPropertyEventCallback {
        override fun onChangeEvent(value: CarPropertyValue<Any>) {
            val speedMs = value.value as Float
            val speedKmH = speedMs.times(3600).div(1000)
            Log.d(TAG, "Car Speed : ${speedKmH}")
            if(speedKmH > 140 && !NotifHunShowed){
                sendNotification140()
                NotifHunShowed = true
            } else if (speedKmH <= 140 && NotifHunShowed) {
                clearNotification140()
                NotifHunShowed = false
            }
        }

        override fun onErrorEvent(propId: Int, zone: Int) {
            Log.w(TAG, "Error, propId=$propId")
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return speedingServiceBinder;
    }

    override fun onCreate() {
        Log.i(TAG, "Create Speeding Service")
        mCar = Car.createCar(this);
        mCarPropertyManager = mCar.getCarManager(Car.PROPERTY_SERVICE) as CarPropertyManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "Speeding Service Start")
        readSpeed()
        return START_STICKY;
    }

    override fun onDestroy() {
        Log.i(TAG, "Speeding Service Destroy")
        stopReadingSpeed()
        mCar.disconnect()
        super.onDestroy()
    }

    private fun readSpeed(){
        Log.i(TAG, "readSpeed")
        mCarPropertyManager.registerCallback(
            carPropertyListener,
            VehiclePropertyIds.PERF_VEHICLE_SPEED,
            CarPropertyManager.SENSOR_RATE_UI
        )
    }

    private fun stopReadingSpeed() {
        mCarPropertyManager.unregisterCallback(carPropertyListener)
    }

    private fun sendNotification140() {
        val NOTIFICATION_CHANNEL_ID = "SpeedingNotification00"
        val NOTIFICATION_CHANNEL_NAME = "SpeedingNotification"
        val NOTIFICATION_IMPORTANCE = NotificationManager.IMPORTANCE_HIGH
        val NOTIFICATION_CATEGORY_HUN = NotificationCompat.CATEGORY_NAVIGATION
        val NOTIFICATION_CATEGORY_CENTER = NotificationCompat.CATEGORY_MESSAGE

        val carNotificationManager = CarNotificationManager.from(this)

        val channel = NotificationChannelCompat.Builder(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_IMPORTANCE
        ).setName(NOTIFICATION_CHANNEL_NAME)
            .setImportance(NOTIFICATION_IMPORTANCE).build()
        carNotificationManager.createNotificationChannel(channel)

        carNotificationManager.notify(1001, getNotificationBuilder(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CATEGORY_HUN))
        carNotificationManager.notify(1002, getNotificationBuilder(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CATEGORY_CENTER))
    }

    private fun clearNotification140() {
        val carNotificationManager = CarNotificationManager.from(this)
        carNotificationManager.cancel(1001)
        carNotificationManager.cancel(1002)
    }

    private fun getNotificationBuilder(channelId : String, category: String): NotificationCompat.Builder {
        val NOTIFICATION_IMPORTANCE = NotificationManager.IMPORTANCE_HIGH

        return NotificationCompat.Builder(this, channelId)
            .setCategory(category)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.drawable.baseline_speed_24)
            .setContentTitle(getString(R.string.notif_title))
            .setContentText(getString(R.string.notif_text))
            .extend(
                CarAppExtender.Builder()
                    .setImportance(NOTIFICATION_IMPORTANCE)
                    .setSmallIcon(R.drawable.baseline_speed_24)
                    .setContentTitle(getString(R.string.notif_title))
                    .setContentText(getString(R.string.notif_text))
                    .build()
            )
    }
}