package com.app.mobile.di.screens

import com.app.mobile.presentation.ui.screens.notification.details.viewmodel.NotificationDetailViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val notificationDetailModule = module {
    viewModelOf(::NotificationDetailViewModel)
}

