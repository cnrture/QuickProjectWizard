package com.github.cnrture.quickprojectwizard.toolwindow.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.cnrture.quickprojectwizard.toolwindow.theme.QPWTheme

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun QPWRadioButton(
    text: String,
    selected: Boolean,
    isBackgroundEnable: Boolean = false,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .selectable(
                selected = selected,
                role = Role.RadioButton,
                onClick = onClick,
            )
            .then(
                if (isBackgroundEnable && selected) {
                    Modifier.background(
                        color = QPWTheme.colors.blue,
                        shape = RoundedCornerShape(12.dp)
                    )
                } else {
                    Modifier
                }
            )
            .clip(RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CompositionLocalProvider(
            LocalMinimumInteractiveComponentEnforcement provides false,
        ) {
            RadioButton(
                colors = RadioButtonDefaults.colors(
                    selectedColor = QPWTheme.colors.white,
                    unselectedColor = QPWTheme.colors.white,
                ),
                selected = selected,
                onClick = onClick,
            )
        }
        Spacer(modifier = Modifier.size(8.dp))
        QPWText(
            text = text,
            color = QPWTheme.colors.white,
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
            ),
        )
    }
}