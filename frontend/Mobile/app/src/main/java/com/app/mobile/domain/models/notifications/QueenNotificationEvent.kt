package com.app.mobile.domain.models.notifications

data class QueenNotificationEvent(
    val queenName: String,
    val eventKey: String,
    val title: String,
    val body: String,
    val isCritical: Boolean
)
