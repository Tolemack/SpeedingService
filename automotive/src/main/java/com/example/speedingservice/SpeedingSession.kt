package com.example.speedingservice

import android.content.Intent
import android.util.Log
import androidx.car.app.CarAppPermission
import androidx.car.app.CarToast
import androidx.car.app.OnRequestPermissionsListener
import androidx.car.app.Screen
import androidx.car.app.Session
import androidx.car.app.annotations.ExperimentalCarApi
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager

@ExperimentalCarApi
class SpeedingSession : Session() {

    private val TAG = "SpeedingSession"

    override fun onCreateScreen(intent: Intent): Screen {
        return SpeedingScreen(carContext)
    }
}