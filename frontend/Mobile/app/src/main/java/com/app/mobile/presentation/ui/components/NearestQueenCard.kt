package com.app.mobile.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.app.mobile.presentation.models.queen.QueenPreviewModel
import com.app.mobile.ui.theme.Alpha
import com.app.mobile.ui.theme.Dimens

@Composable
fun NearestQueenCard(
    queen: QueenPreviewModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.width(150.dp),
        shape = RoundedCornerShape(Dimens.ItemCardRadius),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(Dimens.BorderWidthNormal, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier
                .padding(Dimens.ItemCardPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.ItemsSpacingSmall)
        ) {
            Text(
                text = queen.stage.description,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "улей: ${queen.hiveName ?: "—"}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.Medium),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = queen.stage.remainingDays,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.Disabled),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


