package com.app.mobile.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.app.mobile.domain.repository.notifications.QueenCalendarScheduler
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class QueenCalendarSchedulerImpl(
    private val context: Context
) : QueenCalendarScheduler {

    override fun schedule() {
        val request = PeriodicWorkRequestBuilder<QueenCalendarWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(computeDelayToNextNineAm(), TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            QueenCalendarWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    override fun cancel() {
        WorkManager.getInstance(context).cancelUniqueWork(QueenCalendarWorker.WORK_NAME)
    }

    private fun computeDelayToNextNineAm(): Long {
        val now = LocalDateTime.now()
        val target = now.toLocalDate().atTime(9, 0)
        val next = if (now.isBefore(target)) target else target.plusDays(1)
        return Duration.between(now, next).toMillis()
    }
}
