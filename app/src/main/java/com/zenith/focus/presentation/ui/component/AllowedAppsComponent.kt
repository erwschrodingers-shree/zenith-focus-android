package com.zenith.focus.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenith.focus.data.model.AppInfo

/**
 * Dock for displaying selected allowed apps (max 6)
 */
@Composable
fun AllowedAppsDock(
    selectedApps: List<AppInfo>,
    onAppRemove: (AppInfo) -> Unit,
    onAddApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Allowed Apps (Max 6)",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(6) { index ->
                if (index < selectedApps.size) {
                    AppIconSlot(
                        app = selectedApps[index],
                        onRemove = { onAppRemove(selectedApps[index]) }
                    )
                } else {
                    AddAppSlot(onAdd = onAddApp)
                }
            }
        }
    }
}

@Composable
fun AppIconSlot(
    app: AppInfo,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(80.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.medium
            )
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.medium
            )
            .clickable { onRemove() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(8.dp)
        ) {
            // App icon placeholder (in real implementation, load actual icon)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.small
                    )
            )
            Text(
                text = app.appName.take(6),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 4.dp),
                maxLines = 1
            )
        }
    }
}

@Composable
fun AddAppSlot(
    onAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(80.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.medium
            )
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            )
            .clickable { onAdd() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "+",
            fontSize = 32.sp,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
