package com.github.cnrture.quickprojectwizard.dialog

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.unit.dp
import com.intellij.openapi.externalSystem.model.ProjectSystemId
import com.intellij.openapi.externalSystem.service.execution.ProgressExecutionMode
import com.intellij.openapi.externalSystem.util.ExternalSystemUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.annotations.Nullable
import java.awt.event.ActionEvent
import java.io.File
import java.nio.file.Path
import javax.swing.AbstractAction
import javax.swing.Action
import javax.swing.JComponent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val blue = Color(0xFF568BD4)
val white = Color(0xFFFFFFFF)

const val ANDROID = "Android"

private const val DEFAULT_SRC_VALUE = "EMPTY"

class ModuleMakerDialogWrapper(
    private val project: Project,
    private val startingLocation: VirtualFile?
) : DialogWrapper(true) {

    private val preferenceService = PreferenceServiceImpl.instance

    private val fileWriter = FileWriter(
        preferenceService = preferenceService
    )

    private var selectedSrcValue = mutableStateOf(DEFAULT_SRC_VALUE)
    private val threeModuleCreation = mutableStateOf(preferenceService.preferenceState.threeModuleCreationDefault)
    private val useKtsExtension = mutableStateOf(preferenceService.preferenceState.useKtsFileExtension)
    private val gradleFileNamedAfterModule =
        mutableStateOf(preferenceService.preferenceState.gradleFileNamedAfterModule)
    private val addReadme = mutableStateOf(preferenceService.preferenceState.addReadme)
    private val addGitIgnore = mutableStateOf(preferenceService.preferenceState.addGitIgnore)
    private val moduleTypeSelection = mutableStateOf(ANDROID)
    private val moduleName = mutableStateOf("")
    private val packageName = mutableStateOf(preferenceService.preferenceState.packageName)

    init {
        title = "Quick Project Wizard"
        init()

        selectedSrcValue.value = if (startingLocation != null) {
            // give default of starting location
            File(startingLocation.path).absolutePath.removePrefix(rootDirectoryStringDropLast())
                .removePrefix(File.separator)
        } else {
            // give default value of the root project
            File(rootDirectoryString()).absolutePath.removePrefix(rootDirectoryStringDropLast())
                .removePrefix(File.separator)
        }
    }

    @Nullable
    override fun createCenterPanel(): JComponent {
        return ComposePanel().apply {
            setContent {
                WidgetTheme {
                    val titleList = listOf(
                        "Navigation", "DI", "Local", "Network", "Image", "Lint", "WorkManager",
                        "Firebase", "Java & JVM Version", "Packages",
                    )
                    var navigationState by remember { mutableStateOf(false) }
                    var diState by remember { mutableStateOf(false) }
                    var localState by remember { mutableStateOf(false) }

                    val networkList = listOf("Retrofit", "Ktor Client")
                    var networkState by remember { mutableIntStateOf(0) }

                    val imageList = listOf("Glide", "Coil")
                    var imageState by remember { mutableIntStateOf(0) }

                    var ktLintState by remember { mutableStateOf(false) }
                    var detektState by remember { mutableStateOf(false) }

                    var workManagerState by remember { mutableStateOf(false) }
                    var firebaseState by remember { mutableStateOf(false) }
                    var packagesState by remember { mutableStateOf(false) }
                    var screens by remember { mutableStateOf("") }
                    var javaJvmVersion by remember { mutableStateOf("") }

                    Surface(
                        color = Color.Black,
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    painter = painterResource("pluginIcon.svg"),
                                    contentDescription = "",
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(80.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "Quick Project Wizard",
                                    color = white,
                                    fontSize = 48.sp,
                                    modifier = Modifier.padding(16.dp),
                                    fontWeight = FontWeight.Bold,
                                )
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            Row {
                                Column(
                                    modifier = Modifier.width(260.dp),
                                ) {
                                    titleList.forEach { title ->
                                        QPWTitle(
                                            title = title,
                                            color = blue,
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    QPWCheckbox(
                                        title = "Jetpack Navigation",
                                        checked = navigationState,
                                        onCheckedChange = { navigationState = it },
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    QPWCheckbox(
                                        title = "Hilt",
                                        checked = diState,
                                        onCheckedChange = { diState = it },
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    QPWCheckbox(
                                        title = "Room",
                                        checked = localState,
                                        onCheckedChange = { localState = it },
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    QPWDropdown(
                                        items = networkList,
                                        selectedIndex = networkState,
                                        onItemSelected = { networkState = it },
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    QPWDropdown(
                                        items = imageList,
                                        selectedIndex = imageState,
                                        onItemSelected = { imageState = it },
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        QPWCheckbox(
                                            title = "KtLint",
                                            checked = ktLintState,
                                            onCheckedChange = { ktLintState = it },
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))
                                        QPWCheckbox(
                                            title = "Detekt",
                                            checked = detektState,
                                            onCheckedChange = { detektState = it },
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                    QPWCheckbox(
                                        title = "WorkManager",
                                        checked = workManagerState,
                                        onCheckedChange = { workManagerState = it },
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    QPWCheckbox(
                                        title = "Firebase",
                                        checked = firebaseState,
                                        onCheckedChange = { firebaseState = it },
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    QPWTextField(
                                        color = blue,
                                        hint = "8 or 11 or 17 etc.",
                                        value = javaJvmVersion,
                                        onValueChange = { javaJvmVersion = it },
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    QPWCheckbox(
                                        title = "common - data - domain - ui",
                                        checked = packagesState,
                                        onCheckedChange = { packagesState = it },
                                    )
                                    if (packagesState) {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Row(
                                            modifier = Modifier.height(70.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            QPWTextField(
                                                color = blue,
                                                hint = "Home, Detail, Profile",
                                                value = screens,
                                                onValueChange = { screens = it },
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .height(70.dp)
                                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                                contentAlignment = Alignment.Center,
                                            ) {
                                                Text(
                                                    text = "Please enter the screens you want to create.\nHome, Detail, Profile etc.",
                                                    color = blue,
                                                    fontSize = 22.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Button(
                                    onClick = { },
                                    modifier = Modifier
                                        .height(70.dp)
                                        .width(160.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = blue,
                                        contentColor = white,
                                    ),
                                ) {
                                    Text(
                                        text = "Cancel",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Button(
                                    onClick = { },
                                    modifier = Modifier
                                        .height(70.dp)
                                        .width(160.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = blue,
                                        contentColor = white,
                                    ),
                                ) {
                                    Text(
                                        text = "Apply",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun QPWCheckbox(
        title: String,
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
    ) {
        Row(
            modifier = Modifier
                .height(70.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(white)
                .border(
                    width = 2.dp,
                    color = blue,
                    shape = RoundedCornerShape(12.dp),
                )
                .padding(end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = blue,
                    uncheckedColor = blue,
                ),
            )
            Text(
                text = title,
                color = blue,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }

    @Composable
    private fun QPWTitle(
        title: String,
        color: Color,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                color = white,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }

    @Composable
    private fun QPWTextField(
        color: Color,
        hint: String,
        value: String,
        onValueChange: (String) -> Unit,
    ) {
        Box(
            modifier = Modifier
                .height(70.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(
                    width = 2.dp,
                    color = blue,
                    shape = RoundedCornerShape(12.dp),
                )
                .background(white),
            contentAlignment = Alignment.Center
        ) {
            TextField(
                modifier = Modifier.fillMaxHeight(),
                value = value,
                onValueChange = onValueChange,
                placeholder = {
                    Text(
                        text = hint,
                        color = color,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    textColor = color,
                    backgroundColor = white,
                ),
                textStyle = TextStyle(
                    color = color,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )
        }
    }

    @Composable
    private fun QPWDropdown(
        items: List<String>,
        selectedIndex: Int,
        onItemSelected: (Int) -> Unit,
    ) {
        var dropControl by remember { mutableStateOf(false) }

        Card {
            Row(horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(70.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(white)
                    .border(
                        width = 2.dp,
                        color = blue,
                        shape = RoundedCornerShape(12.dp),
                    )
                    .clickable { dropControl = true }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = items[selectedIndex],
                    fontSize = 24.sp,
                    color = blue,
                    fontWeight = FontWeight.Bold,
                )
                Icon(
                    modifier = Modifier.size(36.dp),
                    imageVector = Icons.Rounded.ArrowDropDown,
                    tint = blue,
                    contentDescription = ""
                )
            }
            DropdownMenu(expanded = dropControl, onDismissRequest = { dropControl = false }) {
                items.forEachIndexed { index, strings ->
                    DropdownMenuItem(
                        onClick = {
                            dropControl = false
                            onItemSelected(index)
                        },
                        content = {
                            Text(
                                text = strings,
                                fontSize = 24.sp,
                            )
                        }
                    )
                }
            }
        }
    }

    override fun createLeftSideActions(): Array<Action> {
        return arrayOf(object : AbstractAction("Settings") {
            override fun actionPerformed(e: ActionEvent?) {

            }
        })
    }

    private fun onSettingsSaved() {
        packageName.value = preferenceService.preferenceState.packageName
        threeModuleCreation.value = preferenceService.preferenceState.threeModuleCreationDefault
        useKtsExtension.value = preferenceService.preferenceState.useKtsFileExtension
        gradleFileNamedAfterModule.value = preferenceService.preferenceState.gradleFileNamedAfterModule
        addReadme.value = preferenceService.preferenceState.addReadme
        addGitIgnore.value = preferenceService.preferenceState.addGitIgnore
    }

    override fun createActions(): Array<Action> {
        return arrayOf(
            DialogWrapperExitAction(
                "Cancel",
                2
            ),
            object : AbstractAction("Create") {
                override fun actionPerformed(e: ActionEvent?) {

                }
            }
        )
    }

    /**
     * When grabbing the settings.gradle(.kts) file, we first want to look in the selected root
     *
     * This is helpful in case of multi-application projects.
     */
    private fun getSettingsGradleFile(): File? {
        val settingsGradleKtsCurrentlySelectedRoot =
            Path.of(getCurrentlySelectedFile().absolutePath, "settings.gradle.kts").toFile()
        val settingsGradleCurrentlySelectedRoot =
            Path.of(getCurrentlySelectedFile().absolutePath, "settings.gradle").toFile()
        val settingsGradleKtsPath = Path.of(rootDirectoryString(), "settings.gradle.kts").toFile()
        val settingsGradlePath = Path.of(rootDirectoryString(), "settings.gradle").toFile()

        return listOf(
            settingsGradleKtsCurrentlySelectedRoot,
            settingsGradleCurrentlySelectedRoot,
            settingsGradleKtsPath,
            settingsGradlePath
        ).firstOrNull {
            it.exists()
        } ?: run {
            //MessageDialogWrapper("Can't find settings.gradle(.kts) file")
            null
        }
    }

    private fun create() {
        val settingsGradleFile = getSettingsGradleFile()
        val moduleType = moduleTypeSelection.value
        val currentlySelectedFile = getCurrentlySelectedFile()
        if (settingsGradleFile != null) {
            fileWriter.createModule(
                // at this point, selectedSrcValue has a value of something like /root/module/module2/
                // - we want to remove the root of the project to use as the file path in settings.gradle
                rootPathString = removeRootFromPath(selectedSrcValue.value),
                settingsGradleFile = settingsGradleFile,
                modulePathAsString = moduleName.value,
                moduleType = moduleType,
                showErrorDialog = {
                    //MessageDialogWrapper(it).show()
                },
                showSuccessDialog = {
                    //MessageDialogWrapper("Success").show()
                    refreshFileSystem(
                        settingsGradleFile = settingsGradleFile,
                        currentlySelectedFile = currentlySelectedFile
                    )
                    if (preferenceService.preferenceState.refreshOnModuleAdd) {
                        syncProject()
                    }
                },
                workingDirectory = currentlySelectedFile,
                enhancedModuleCreationStrategy = threeModuleCreation.value,
                useKtsBuildFile = useKtsExtension.value,
                gradleFileFollowModule = gradleFileNamedAfterModule.value,
                packageName = packageName.value,
                addReadme = addReadme.value,
                addGitIgnore = addGitIgnore.value
            )
        } else {
            //MessageDialogWrapper("Couldn't find settings.gradle(.kts)").show()
        }
    }

    private fun syncProject() {
        ExternalSystemUtil.refreshProject(
            project,
            ProjectSystemId("GRADLE"),
            rootDirectoryString(),
            false,
            ProgressExecutionMode.START_IN_FOREGROUND_ASYNC
        )
    }

    /**
     * Refresh the settings gradle file and the root file
     */
    private fun refreshFileSystem(settingsGradleFile: File, currentlySelectedFile: File) {
        VfsUtil.markDirtyAndRefresh(
            false,
            true,
            true,
            settingsGradleFile,
            currentlySelectedFile
        )
    }

    private fun getCurrentlySelectedFile(): File {
        return File(rootDirectoryStringDropLast() + File.separator + selectedSrcValue.value)
    }

    private fun rootDirectoryStringDropLast(): String {
        // rootDirectoryString() gives us back something like /Users/user/path/to/project
        // the first path element in the tree node starts with 'project' (last folder above)
        // so we remove it and join the nodes of the tree by our file separator
        return project.basePath!!.split(File.separator).dropLast(1).joinToString(File.separator)
    }

    private fun rootDirectoryString(): String {
        return project.basePath!!
    }

    private fun removeRootFromPath(path: String): String {
        return path.split(File.separator).drop(1).joinToString(File.separator)
    }
}