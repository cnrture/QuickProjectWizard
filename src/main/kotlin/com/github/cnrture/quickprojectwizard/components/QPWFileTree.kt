package com.github.cnrture.quickprojectwizard.components

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material.icons.automirrored.filled.TextSnippet
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.cnrture.quickprojectwizard.file.ExpandableFile
import com.github.cnrture.quickprojectwizard.file.FileTree
import com.github.cnrture.quickprojectwizard.theme.QPWTheme

@Composable
fun QPWFileTree(
    modifier: Modifier = Modifier,
    model: FileTree,
    onClick: (ExpandableFile) -> Unit,
) {
    Surface(
        modifier = modifier,
        color = QPWTheme.colors.gray,
    ) {
        Column {
            QPWText(
                text = "Project File Tree",
                color = QPWTheme.colors.orange,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
            )
            Spacer(modifier = Modifier.size(16.dp))
            with(LocalDensity.current) {
                Box {
                    val lazyListState = rememberLazyListState()
                    val scrollState = rememberScrollState()

                    LazyColumn(
                        modifier = Modifier.fillMaxSize().horizontalScroll(scrollState),
                        state = lazyListState
                    ) {
                        items(model.items.size) {
                            FileTreeItemView(
                                model = model.items[it],
                                height = 14.sp.toDp() * 1.5f,
                                showBottomPadding = it == model.items.size - 1 &&
                                    (lazyListState.canScrollForward || lazyListState.canScrollBackward),
                                showEndPadding = scrollState.canScrollForward || scrollState.canScrollBackward,
                                onClick = onClick,
                            )
                        }
                    }

                    VerticalScrollbar(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        adapter = rememberScrollbarAdapter(lazyListState),
                        style = defaultScrollbarStyle().copy(
                            unhoverColor = QPWTheme.colors.white.copy(alpha = 0.2f),
                            hoverColor = QPWTheme.colors.white.copy(alpha = 0.6f),
                        )
                    )

                    HorizontalScrollbar(
                        modifier = Modifier.align(Alignment.BottomStart),
                        adapter = rememberScrollbarAdapter(scrollState),
                        style = defaultScrollbarStyle().copy(
                            unhoverColor = QPWTheme.colors.white.copy(alpha = 0.2f),
                            hoverColor = QPWTheme.colors.white.copy(alpha = 0.6f),
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun FileTreeItemView(
    height: Dp,
    model: FileTree.Item,
    onClick: (ExpandableFile) -> Unit,
    showBottomPadding: Boolean,
    showEndPadding: Boolean,
) =
    Row(
        modifier = Modifier
            .wrapContentHeight()
            .clickable {
                model.open()
                onClick(model.file)
            }
            .padding(
                start = 12.dp * model.level,
                end = if (showEndPadding) 8.dp else 0.dp,
                bottom = if (showBottomPadding) 8.dp else 0.dp
            )
            .height(height)
            .fillMaxWidth()
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        val active by interactionSource.collectIsHoveredAsState()

        FileItemIcon(
            modifier = Modifier.align(Alignment.CenterVertically),
            model = model,
        )
        QPWText(
            text = model.name,
            color = if (active) QPWTheme.colors.white.copy(alpha = 0.60f) else QPWTheme.colors.white,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .clipToBounds()
                .hoverable(interactionSource),
            style = TextStyle(
                fontSize = 16.sp,
            ),
            softWrap = true,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )
    }

@Composable
private fun FileItemIcon(modifier: Modifier, model: FileTree.Item) {
    Box(
        modifier = modifier
            .size(24.dp)
            .padding(4.dp),
    ) {
        when (val type = model.type) {
            is FileTree.ItemType.Folder -> when {
                !type.canExpand -> Unit
                type.isExpanded -> Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = QPWTheme.colors.white,
                )

                else -> Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = QPWTheme.colors.white,
                )
            }

            is FileTree.ItemType.File -> when (type.ext) {
                in sourceCodeFileExtensions -> Icon(
                    imageVector = Icons.Default.Code,
                    contentDescription = null,
                    tint = Color(0xFF3E86A0),
                )

                "txt" -> Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = Color(0xFF87939A),
                )

                "md" -> Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = Color(0xFF87939A),
                )

                "gitignore" -> Icon(
                    imageVector = Icons.Default.BrokenImage,
                    contentDescription = null,
                    tint = Color(0xFF87939A),
                )

                "gradle" -> Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = null,
                    tint = Color(0xFF87939A),
                )

                "kts" -> Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = null,
                    tint = Color(0xFF3E86A0),
                )

                "properties" -> Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = Color(0xFF62B543),
                )

                "bat" -> Icon(
                    imageVector = Icons.AutoMirrored.Filled.Launch,
                    contentDescription = null,
                    tint = Color(0xFF87939A),
                )

                else -> Icon(
                    imageVector = Icons.AutoMirrored.Filled.TextSnippet,
                    contentDescription = null,
                    tint = Color(0xFF87939A),
                )
            }
        }
    }
}

private val sourceCodeFileExtensions = listOf("java", "kt", "xml")