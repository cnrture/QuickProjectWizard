package com.github.cnrture.quickprojectwizard.dialog

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
import com.github.cnrture.quickprojectwizard.components.QPWButton
import com.github.cnrture.quickprojectwizard.components.QPWDialogWrapper
import com.github.cnrture.quickprojectwizard.components.QPWText
import com.github.cnrture.quickprojectwizard.theme.QPWTheme

class MessageDialog(private val message: String) : QPWDialogWrapper() {

    @Composable
    override fun createDesign() {
        Column(
            modifier = Modifier.padding(vertical = 24.dp, horizontal = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            QPWText(
                text = message,
                color = QPWTheme.colors.white,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                ),
            )
            Spacer(modifier = Modifier.size(24.dp))
            QPWButton(
                text = "Okay",
                onClick = { close(Constants.DEFAULT_EXIT_CODE) },
                backgroundColor = QPWTheme.colors.red,
            )
        }
    }
}