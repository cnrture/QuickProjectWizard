package com.github.cnrture.quickprojectwizard.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.cnrture.quickprojectwizard.common.Constants
import com.github.cnrture.quickprojectwizard.theme.QPWTheme

class QPWMessageDialog(private val message: String) : QPWDialogWrapper() {

    @Composable
    override fun createDesign() {
        Column(
            modifier = Modifier.Companion.padding(vertical = 24.dp, horizontal = 64.dp),
            horizontalAlignment = Alignment.Companion.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            QPWText(
                text = message,
                color = QPWTheme.colors.white,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Companion.SemiBold,
                    textAlign = TextAlign.Companion.Center,
                ),
            )
            Spacer(modifier = Modifier.Companion.size(24.dp))
            QPWButton(
                text = "Okay",
                onClick = { close(Constants.DEFAULT_EXIT_CODE) },
                backgroundColor = QPWTheme.colors.red,
            )
        }
    }
}