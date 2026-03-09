package com.app.mobile.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.app.mobile.R
import com.app.mobile.presentation.models.notification.NotificationPreviewModel
import com.app.mobile.ui.theme.Alpha
import com.app.mobile.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("DEPRECATION")
@Composable
fun SwipeableNotificationCard(
    notification: NotificationPreviewModel,
    onClick: () -> Unit,
    onSwipeDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                onSwipeDismiss()
            }
            false // Не подтверждаем dismiss — просто визуальный свайп
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                        RoundedCornerShape(Dimens.ItemCardRadius)
                    )
                    .padding(end = Dimens.ScreenContentPadding),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_trash),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(Dimens.IconSizeLarge)
                )
            }
        },
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        modifier = modifier
    ) {
        NotificationCard(
            notification = notification,
            onClick = onClick
        )
    }
}

@Composable
fun NotificationCard(
    notification: NotificationPreviewModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = when {
        notification.isUrgent -> MaterialTheme.colorScheme.error
        notification.isNew -> MaterialTheme.colorScheme.primary
        else -> Color.Transparent
    }

    val borderStroke = if (borderColor != Color.Transparent) {
        BorderStroke(Dimens.BorderWidthNormal, borderColor)
    } else {
        null
    }

    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.ItemCardRadius),
        color = MaterialTheme.colorScheme.surface,
        border = borderStroke
    ) {
        Column(
            modifier = Modifier
                .padding(Dimens.ItemCardPadding)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Dimens.ItemCardTextPadding)
        ) {
            Text(
                text = notification.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = notification.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = notification.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.Disabled)
                )
                Text(
                    text = "Улей: ${notification.hiveName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.Disabled),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}



