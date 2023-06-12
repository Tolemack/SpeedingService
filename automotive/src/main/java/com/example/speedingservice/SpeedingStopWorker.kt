package com.example.speedingservice

import android.content.Context
import android.content.Intent
import androidx.work.Worker
import androidx.work.WorkerParameters

class SpeedingStopWorker(private val ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    override fun doWork(): Result {

        try {
            Intent(ctx, SpeedingService::class.java).also { intent ->
                ctx.stopService(intent)
            }
        } catch(t:Throwable){
            return Result.failure()
        }

        return Result.success()
    }
}