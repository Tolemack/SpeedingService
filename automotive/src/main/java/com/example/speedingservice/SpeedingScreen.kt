package com.example.speedingservice

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.CarColor
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Template

class SpeedingScreen(carContext: CarContext) : Screen(carContext) {

    private val TAG = "SpeedingScreen"

    private val sssch = SpeedingServiceScheduler(carContext)

    private var startButton: Action =
        Action.Builder()
            .setTitle(
                carContext.resources.getString(R.string.button_off)
            )
            .setBackgroundColor(CarColor.YELLOW)
            .setOnClickListener {
                startButtonListener()
            }
            .build()

    private var stopButton: Action =
        Action.Builder()
            .setTitle(
                carContext.resources.getString(R.string.button_on)
            )
            .setBackgroundColor(CarColor.GREEN)
            .setOnClickListener {
                stopButtonListener()
            }
            .build()

    private var permissionButton: Action =
        Action.Builder()
            .setTitle(
                carContext.resources.getString(R.string.button_request)
            )
            .setBackgroundColor(CarColor.BLUE)
            .setOnClickListener {
                askPermissionListener()
            }
            .build()

    // This class allows Screen's invalidation after the user
    // approves the CAR_SPEED permission
    inner class RefreshScreen() {
        fun refresh(){
            this@SpeedingScreen.invalidate()
        }
    }

    override fun onGetTemplate(): Template {
        lateinit var button: Action
        lateinit var text: String
        if(!sssch.checkPermission()){
            button = permissionButton
            text = carContext.resources.getString(R.string.permissionRequest)
        } else if (sssch.serviceOn) {
            button = stopButton
            text = carContext.resources.getString(R.string.my_app_name)
        } else {
            button = startButton
            text = carContext.resources.getString(R.string.my_app_name)
        }

        val template = MessageTemplate.Builder(text)
            .setHeaderAction(Action.BACK)
            .addAction(
                button
            )
            .build()

        return template
    }

    private fun startButtonListener(){
        sssch.scheduleSpeedingService()
        invalidate()
    }

    private fun stopButtonListener(){
        sssch.stopSpeedingService()
        invalidate()
    }

    private fun askPermissionListener(){
        sssch.askPermission(RefreshScreen())
    }
}