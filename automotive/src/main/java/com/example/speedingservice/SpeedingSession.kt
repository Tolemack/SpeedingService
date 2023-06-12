package com.example.speedingservice

import android.content.Intent
import androidx.car.app.Screen
import androidx.car.app.Session

class SpeedingSession : Session() {

    private val TAG = "SpeedingSession"

    override fun onCreateScreen(intent: Intent): Screen {
        return SpeedingScreen(carContext)
    }
}