package com.app.mobile.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.app.mobile.domain.usecase.notifications.SendQueenCalendarNotificationsUseCase
import org.koin.java.KoinJavaComponent.inject

class QueenCalendarWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val sendQueenCalendarNotificationsUseCase: SendQueenCalendarNotificationsUseCase
        by inject(SendQueenCalendarNotificationsUseCase::class.java)

    override suspend fun doWork(): Result {
        return try {
            sendQueenCalendarNotificationsUseCase()
            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "queen_calendar_daily_check"
    }
}
