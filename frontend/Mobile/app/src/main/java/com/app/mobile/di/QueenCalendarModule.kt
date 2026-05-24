package com.app.mobile.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.app.mobile.data.datastore.SentNotificationKeyDataSource
import com.app.mobile.data.datastore.SentNotificationKeyDataSourceImpl
import com.app.mobile.data.repository.notifications.SentNotificationKeyRepositoryImpl
import com.app.mobile.domain.repository.notifications.QueenCalendarScheduler
import com.app.mobile.domain.repository.notifications.SentNotificationKeyRepository
import com.app.mobile.domain.usecase.notifications.GetQueenNotificationsForTodayUseCase
import com.app.mobile.domain.usecase.notifications.SendQueenCalendarNotificationsUseCase
import com.app.mobile.worker.QueenCalendarSchedulerImpl
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

private val Context.sentNotificationKeysDataStore: DataStore<Preferences>
    by preferencesDataStore(name = "sent_notification_keys")

val queenCalendarModule = module {

    single<DataStore<Preferences>>(named("SentNotificationKeysStore")) {
        androidContext().sentNotificationKeysDataStore
    }

    single {
        SentNotificationKeyDataSourceImpl(
            dataStore = get(named("SentNotificationKeysStore")),
            json = get()
        )
    } bind SentNotificationKeyDataSource::class

    singleOf(::SentNotificationKeyRepositoryImpl) bind SentNotificationKeyRepository::class

    factoryOf(::GetQueenNotificationsForTodayUseCase)

    factoryOf(::SendQueenCalendarNotificationsUseCase)

    factoryOf(::QueenCalendarSchedulerImpl) bind QueenCalendarScheduler::class
}
