package com.app.mobile.presentation.ui.screens.notification.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.mobile.R
import com.app.mobile.presentation.models.notification.NotificationDetailModel
import com.app.mobile.presentation.ui.components.AppTopBar
import com.app.mobile.presentation.ui.components.ErrorMessage
import com.app.mobile.presentation.ui.components.FullScreenProgressIndicator
import com.app.mobile.presentation.ui.components.ObserveAsEvents
import com.app.mobile.presentation.ui.components.TopBarAction
import com.app.mobile.presentation.ui.screens.notification.details.viewmodel.NotificationDetailEvent
import com.app.mobile.presentation.ui.screens.notification.details.viewmodel.NotificationDetailUiState
import com.app.mobile.presentation.ui.screens.notification.details.viewmodel.NotificationDetailViewModel
import com.app.mobile.ui.theme.Alpha
import com.app.mobile.ui.theme.Dimens

@Composable
fun NotificationDetailScreen(
    viewModel: NotificationDetailViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.loadNotification()
    }

    ObserveAsEvents(viewModel.event) { event ->
        when (event) {
            is NotificationDetailEvent.NavigateBack -> onBackClick()
            is NotificationDetailEvent.ShowSnackBar -> {
                snackbarHostState.showSnackbar(
                    event.message,
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    when (val state = uiState) {
        is NotificationDetailUiState.Loading -> FullScreenProgressIndicator()
        is NotificationDetailUiState.Error -> ErrorMessage(state.message, onRetry = viewModel::resetError)
        is NotificationDetailUiState.Content -> {
            NotificationDetailContent(
                notification = state.notification,
                snackbarHostState = snackbarHostState,
                onBackClick = onBackClick,
                onDeleteClick = viewModel::onDeleteClick
            )
        }
    }
}

@Composable
private fun NotificationDetailContent(
    notification: NotificationDetailModel,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.notification),
                onBackClick = onBackClick,
                action = TopBarAction.Delete(onClick = onDeleteClick)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(Dimens.ScreenContentPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.ItemsSpacingLarge)
        ) {
            // Заголовок + дата
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Dimens.ItemsSpacingSmall)
                ) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "улей: ${notification.hiveName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.Medium)
                    )
                }
                Text(
                    text = notification.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            // Полный текст
            Text(
                text = notification.fullText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

