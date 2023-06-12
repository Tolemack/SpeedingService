package com.example.speedingservice;

import androidx.car.app.CarAppPermission
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.OnRequestPermissionsListener
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager


class SpeedingServiceScheduler(private val carContext: CarContext) {

    private val TAG = "SpeedingServiceScheduler"

    private val requiredPermissions: List<String> = listOf("android.car.permission.CAR_SPEED")
    private val workManager = WorkManager.getInstance(carContext)
    var serviceOn = false;

    // Binding being unecessary for my use case
    /*private val intent: Intent = Intent(carContext, SpeedingService::class.java)
    private var mSpeedingService: SpeedingService? = null
    private val mSpeedingServiceConnection = object: ServiceConnection {
        @androidx.annotation.OptIn(androidx.car.app.annotations.ExperimentalCarApi::class)
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as SpeedingService.SpeedingServiceBinder
            mSpeedingService = binder.getService()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            Log.i(TAG, "SERVICE DISCONNECTED")
        }
    }*/

    fun checkPermission(): Boolean {
        try {
            CarAppPermission.checkHasPermission(carContext, requiredPermissions.get(0))
            return true;
        } catch (e: SecurityException) {
            return false
        }
    }

    fun askPermission(rs: SpeedingScreen.RefreshScreen) {
        carContext.requestPermissions(
            requiredPermissions,
            OnRequestPermissionsListener {
                    approved: List<String?>?, rejected: List<String?>? ->
                if (approved != null && approved.equals(requiredPermissions)) {
                    CarToast.makeText(
                        carContext,
                        "Permission Approved",
                        CarToast.LENGTH_LONG
                    ).show()
                    rs.refresh()
                }
            }
        )
    }

    fun scheduleSpeedingService() {
        workManager.cancelAllWork()
        if (checkPermission()) {
            workManager.beginUniqueWork(
                "SpeedingStartWork",
                ExistingWorkPolicy.KEEP,
                OneTimeWorkRequest.from(SpeedingStartWorker::class.java)
            ).enqueue()
            //carContext.bindService(intent, mSpeedingServiceConnection, Context.BIND_AUTO_CREATE)
            serviceOn = true
        } else {
            serviceOn = false
        }
    }

    fun stopSpeedingService() {
        //carContext.unbindService(mSpeedingServiceConnection)
        workManager.beginUniqueWork(
            "SpeedingStopWork",
            ExistingWorkPolicy.KEEP,
            OneTimeWorkRequest.from(SpeedingStopWorker::class.java)
        ).enqueue()
        serviceOn = false
    }
}
