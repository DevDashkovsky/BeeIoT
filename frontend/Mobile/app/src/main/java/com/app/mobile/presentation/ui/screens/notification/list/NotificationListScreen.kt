package com.app.mobile.presentation.ui.screens.notification.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.mobile.R
import com.app.mobile.presentation.models.notification.NotificationPreviewModel
import com.app.mobile.presentation.models.queen.QueenPreviewModel
import com.app.mobile.presentation.ui.components.EmptyStub
import com.app.mobile.presentation.ui.components.ErrorMessage
import com.app.mobile.presentation.ui.components.FullScreenProgressIndicator
import com.app.mobile.presentation.ui.components.NearestQueenCard
import com.app.mobile.presentation.ui.components.NotificationListTopBar
import com.app.mobile.presentation.ui.components.ObserveAsEvents
import com.app.mobile.presentation.ui.components.SectionHeaderWithAction
import com.app.mobile.presentation.ui.components.SectionTitle
import com.app.mobile.presentation.ui.components.SwipeableNotificationCard
import com.app.mobile.presentation.ui.screens.notification.list.models.NotificationListActions
import com.app.mobile.presentation.ui.screens.notification.list.viewmodel.NotificationListEvent
import com.app.mobile.presentation.ui.screens.notification.list.viewmodel.NotificationListUiState
import com.app.mobile.presentation.ui.screens.notification.list.viewmodel.NotificationListViewModel
import com.app.mobile.ui.theme.Dimens

@Composable
fun NotificationListScreen(
    viewModel: NotificationListViewModel,
    onNotificationClick: (String) -> Unit,
    onQueenClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.loadNotifications()
    }

    ObserveAsEvents(viewModel.event) { event ->
        when (event) {
            is NotificationListEvent.NavigateToNotification -> onNotificationClick(event.notificationId)
            is NotificationListEvent.ShowSnackBar -> {
                snackbarHostState.showSnackbar(
                    event.message,
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    when (val state = uiState) {
        is NotificationListUiState.Loading -> FullScreenProgressIndicator()

        is NotificationListUiState.Error -> ErrorMessage(state.message, onRetry = viewModel::resetError)

        is NotificationListUiState.Content -> {
            val actions = NotificationListActions(
                onNotificationClick = viewModel::onNotificationClick,
                onDeleteAll = viewModel::onDeleteAll,
                onQueenClick = { queenId -> onQueenClick(queenId) }
            )

            NotificationListContent(
                nearestQueens = state.nearestQueens,
                notifications = state.notifications,
                snackbarHostState = snackbarHostState,
                actions = actions,
                onSwipeDismiss = viewModel::onSwipeDismiss
            )
        }
    }
}

@Composable
private fun NotificationListContent(
    nearestQueens: List<QueenPreviewModel>,
    notifications: List<NotificationPreviewModel>,
    snackbarHostState: SnackbarHostState,
    actions: NotificationListActions,
    onSwipeDismiss: (String) -> Unit
) {
    val isEmpty = nearestQueens.isEmpty() && notifications.isEmpty()

    Scaffold(
        topBar = {
            NotificationListTopBar(
                title = stringResource(R.string.notifications),
                actionText = stringResource(R.string.delete_all),
                onActionClick = actions.onDeleteAll
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) { innerPadding ->
        if (isEmpty) {
            EmptyStub(
                text = stringResource(R.string.empty_notifications),
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(
                    top = Dimens.ScreenContentPadding,
                    bottom = Dimens.ScreenContentPadding
                ),
                verticalArrangement = Arrangement.spacedBy(Dimens.ItemsSpacingLarge)
            ) {
                // Секция "Ближайшие матки"
                if (nearestQueens.isNotEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Dimens.ScreenContentPadding),
                            verticalArrangement = Arrangement.spacedBy(Dimens.ItemSpacingNormal)
                        ) {
                            SectionTitle(title = stringResource(R.string.nearest_queens_section))
                        }
                    }

                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(Dimens.ItemSpacingNormal),
                            contentPadding = PaddingValues(horizontal = Dimens.ScreenContentPadding)
                        ) {
                            items(nearestQueens) { queen ->
                                NearestQueenCard(
                                    queen = queen,
                                    onClick = { actions.onQueenClick(queen.id) }
                                )
                            }
                        }
                    }
                }

                // Секция "Уведомления"
                if (notifications.isNotEmpty()) {
                    item {
                        SectionHeaderWithAction(
                            title = stringResource(R.string.notifications),
                            actionText = stringResource(R.string.delete_all),
                            onActionClick = actions.onDeleteAll,
                            modifier = Modifier.padding(horizontal = Dimens.ScreenContentPadding)
                        )
                    }

                    items(notifications, key = { it.id }) { notification ->
                        SwipeableNotificationCard(
                            notification = notification,
                            onClick = { actions.onNotificationClick(notification.id) },
                            onSwipeDismiss = { onSwipeDismiss(notification.id) },
                            modifier = Modifier.padding(horizontal = Dimens.ScreenContentPadding)
                        )
                    }
                }
            }
        }
    }
}

