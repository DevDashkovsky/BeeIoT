package com.app.mobile.di.screens

import com.app.mobile.presentation.ui.screens.notification.list.viewmodel.NotificationListViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val notificationListModule = module {
    viewModelOf(::NotificationListViewModel)
}

