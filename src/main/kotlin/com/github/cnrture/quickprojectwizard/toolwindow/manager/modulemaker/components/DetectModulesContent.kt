package com.github.cnrture.quickprojectwizard.toolwindow.manager.modulemaker.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.cnrture.quickprojectwizard.common.Utils.analyzeSelectedDirectory
import com.github.cnrture.quickprojectwizard.common.getCurrentlySelectedFile
import com.github.cnrture.quickprojectwizard.components.QPWText
import com.github.cnrture.quickprojectwizard.theme.QPWTheme
import com.intellij.openapi.project.Project

@Composable
fun DetectModulesContent(
    project: Project,
    isAnalyzingState: Boolean,
    analysisResultState: String?,
    selectedSrc: String,
    onAnalysisResultChange: (String?) -> Unit,
    onAnalyzingChange: (Boolean) -> Unit,
    onDetectedModulesLoaded: (List<String>) -> Unit,
    onSelectedModulesLoaded: (List<String>) -> Unit,
    detectedModules: List<String>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 2.dp,
                color = QPWTheme.colors.white,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            QPWText(
                text = "Detect Modules",
                color = QPWTheme.colors.white,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
            )
            Spacer(modifier = Modifier.size(8.dp))
            Box {
                if (isAnalyzingState) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = QPWTheme.colors.green,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Icon(
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                val selectedFile = project.getCurrentlySelectedFile(selectedSrc)
                                if (selectedFile.exists()) {
                                    analyzeSelectedDirectory(
                                        directory = selectedFile,
                                        project = project,
                                        onAnalysisResultChange = onAnalysisResultChange,
                                        onAnalyzingChange = onAnalyzingChange,
                                        onDetectedModulesLoaded = onDetectedModulesLoaded,
                                        onSelectedModulesLoaded = onSelectedModulesLoaded,
                                        detectedModules = detectedModules,
                                    )
                                }
                            },
                        imageVector = Icons.Rounded.PlayArrow,
                        tint = QPWTheme.colors.green,
                        contentDescription = null,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.size(4.dp))
        QPWText(
            text = "These modules will be added to the new module's build.gradle file.",
            color = QPWTheme.colors.lightGray,
            style = TextStyle(fontSize = 13.sp),
        )
        Spacer(modifier = Modifier.size(8.dp))
        analysisResultState?.let { result ->
            QPWText(
                text = result,
                color = QPWTheme.colors.green,
            )
        }
    }
}