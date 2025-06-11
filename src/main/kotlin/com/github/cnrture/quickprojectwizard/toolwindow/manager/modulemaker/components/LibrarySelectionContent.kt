package com.github.cnrture.quickprojectwizard.toolwindow.manager.modulemaker.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.cnrture.quickprojectwizard.toolwindow.components.QPWCheckbox
import com.github.cnrture.quickprojectwizard.toolwindow.theme.QPWTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LibrarySelectionContent(
    availableLibraries: List<String>,
    selectedLibraries: List<String>,
    onLibrarySelected: (String) -> Unit,
    libraryGroups: Map<String, List<String>>,
    expandedGroups: Map<String, Boolean>,
    onGroupExpandToggle: (String) -> Unit,
) {
    if (availableLibraries.isNotEmpty()) {
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
                .padding(16.dp)
        ) {
            Text(
                text = "Library Dependencies",
                color = QPWTheme.colors.white,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "Select libraries that your new module will depend on:",
                color = QPWTheme.colors.lightGray,
                fontSize = 14.sp,
            )
            Divider(
                color = QPWTheme.colors.lightGray,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                libraryGroups.forEach { (groupName, groupLibraries) ->
                    val isExpanded = expandedGroups[groupName] ?: false
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onGroupExpandToggle(groupName) }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = groupName,
                            color = QPWTheme.colors.red,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Icon(
                            imageVector = Icons.Rounded.ExpandMore,
                            contentDescription = null,
                            tint = QPWTheme.colors.red,
                            modifier = Modifier
                                .size(24.dp)
                                .rotate(if (isExpanded) 180f else 0f)
                        )
                    }
                    if (isExpanded) {
                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            groupLibraries.forEach { library ->
                                val isChecked = library in selectedLibraries
                                QPWCheckbox(
                                    checked = isChecked,
                                    label = library,
                                    isBackgroundEnable = true,
                                    color = QPWTheme.colors.red,
                                    onCheckedChange = { onLibrarySelected(library) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}