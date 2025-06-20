package com.github.cnrture.quickprojectwizard.toolwindow.manager.modulegenerator.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.cnrture.quickprojectwizard.common.Utils.analyzeSelectedDirectory
import com.github.cnrture.quickprojectwizard.common.getCurrentlySelectedFile
import com.github.cnrture.quickprojectwizard.components.QPWCheckbox
import com.github.cnrture.quickprojectwizard.components.QPWText
import com.github.cnrture.quickprojectwizard.theme.QPWTheme
import com.intellij.openapi.project.Project

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetectedModulesContent(
    project: Project,
    isAnalyzingState: Boolean,
    analysisResultState: String?,
    selectedSrc: String,
    onAnalysisResultChange: (String?) -> Unit,
    onAnalyzingChange: (Boolean) -> Unit,
    onDetectedModulesLoaded: (List<String>) -> Unit,
    onSelectedModulesLoaded: (List<String>) -> Unit,
    detectedModules: List<String>,
    existingModules: List<String>,
    selectedModules: List<String>,
    onCheckedModule: (String) -> Unit,
) {
    if (existingModules.isNotEmpty()) {
        Column(
            modifier = Modifier
                .background(
                    color = QPWTheme.colors.gray,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                QPWText(
                    text = "Detected Modules in Selected Directory",
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
                text = "Select modules that your new module will depend on:",
                color = QPWTheme.colors.lightGray,
                style = TextStyle(fontSize = 13.sp),
            )
            Divider(
                color = QPWTheme.colors.lightGray,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            analysisResultState?.let { result ->
                QPWText(
                    text = result,
                    style = TextStyle(fontSize = 13.sp),
                    color = QPWTheme.colors.green,
                )
                Spacer(modifier = Modifier.size(8.dp))
            }
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                existingModules.forEachIndexed { index, module ->
                    val isChecked = module in selectedModules
                    QPWCheckbox(
                        checked = isChecked,
                        label = module,
                        isBackgroundEnable = true,
                        color = QPWTheme.colors.green,
                        onCheckedChange = { onCheckedModule(module) },
                    )
                }
            }
        }
    }
}