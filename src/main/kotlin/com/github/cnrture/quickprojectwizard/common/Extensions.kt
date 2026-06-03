package com.github.cnrture.quickprojectwizard.common

import com.android.tools.idea.wizard.template.ModuleTemplateData
import com.android.tools.idea.wizard.template.RecipeExecutor
import com.android.tools.idea.wizard.template.Thumb
import com.github.cnrture.quickprojectwizard.projectwizard.gradle.Library
import com.github.cnrture.quickprojectwizard.projectwizard.gradle.Plugin
import com.github.cnrture.quickprojectwizard.projectwizard.gradle.Version
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import java.io.File
import java.net.URI
import com.github.cnrture.quickprojectwizard.common.file.File as ProjectFile

fun Project.getCurrentlySelectedFile(selectedSrc: String): File =
    File(rootDirectoryStringDropLast() + File.separator + selectedSrc)

fun Project.rootDirectoryStringDropLast(): String =
    basePath!!.split(File.separator).dropLast(1).joinToString(File.separator)

fun Project.rootDirectoryString(): String = basePath!!

fun List<File>.refreshFileSystem() {
    VfsUtil.markDirtyAndRefresh(false, true, true, *this.toTypedArray())
}

fun File.toProjectFile(): ProjectFile = object : ProjectFile {
    private val numberOfFiles = listFiles()?.size ?: 0
    override val name: String = this@toProjectFile.name
    override val absolutePath: String = this@toProjectFile.absolutePath
    override val isDirectory: Boolean = this@toProjectFile.isDirectory
    override val hasChildren: Boolean = isDirectory && numberOfFiles > 0
    override val children: List<ProjectFile> = this@toProjectFile
        .listFiles { _, name -> !name.startsWith(".") }
        .orEmpty()
        .map { it.toProjectFile() }
}

fun RecipeExecutor.addRootFile(data: String, moduleData: ModuleTemplateData, dirPath: String) {
    save(data, moduleData.rootDir.parentFile.resolve(dirPath))
}

fun RecipeExecutor.addSrcFile(data: String, moduleData: ModuleTemplateData, filePath: String) {
    save(data, moduleData.srcDir.resolve(filePath))
}

fun StringBuilder.addLibsVersion(version: Version) {
    append("${version.name} = \"${version.value}\"\n")
}

fun StringBuilder.addLibsDependency(library: Library) {
    val version = if (library.verRef == null) "" else ", version.ref = \"${library.verRef}\""
    append("${library.libName} = { group = \"${library.group}\", name = \"${library.name}\"$version }\n")
}

fun StringBuilder.addLibsPlugin(plugin: Plugin) {
    append("${plugin.name} = { id = \"${plugin.id}\", version.ref = \"${plugin.verRef}\" }\n")
}

fun StringBuilder.addGradlePlugin(plugin: Plugin, isProject: Boolean = false, isKts: Boolean = true) {
    val name = plugin.name.replace("-", ".")
    val lastPath = if (isProject) (if (isKts) " apply false" else " apply false") else ""
    if (isKts) {
        append("    alias(libs.plugins.$name)$lastPath\n")
    } else {
        append("    alias libs.plugins.$name$lastPath\n")
    }
}

fun StringBuilder.addGradleImplementation(library: Library, isKts: Boolean = true) {
    val name = library.libName.replace("-", ".")
    if (isKts) {
        append("    implementation(libs.$name)\n")
    } else {
        append("    implementation libs.$name\n")
    }
}

fun StringBuilder.addGradleDetektImplementation(library: Library, isKts: Boolean = true) {
    val name = library.libName.replace("-", ".")
    if (isKts) {
        append("    detektPlugins(libs.$name)\n")
    } else {
        append("    detektPlugins libs.$name\n")
    }
}

fun StringBuilder.addGradlePlatformImplementation(library: Library, isKts: Boolean = true) {
    val name = library.libName.replace("-", ".")
    if (isKts) {
        append("    implementation(platform(libs.$name))\n")
    } else {
        append("    implementation platform(libs.$name)\n")
    }
}

fun StringBuilder.addGradleTestImplementation(library: Library, isKts: Boolean = true) {
    val name = library.libName.replace("-", ".")
    if (isKts) {
        append("    testImplementation(libs.$name)\n")
    } else {
        append("    testImplementation libs.$name\n")
    }
}

fun StringBuilder.addGradleAndroidTestImplementation(library: Library, isKts: Boolean = true) {
    val name = library.libName.replace("-", ".")
    if (isKts) {
        append("    androidTestImplementation(libs.$name)\n")
    } else {
        append("    androidTestImplementation libs.$name\n")
    }
}

fun StringBuilder.addGradleAndroidTestPlatformImplementation(library: Library, isKts: Boolean = true) {
    val name = library.libName.replace("-", ".")
    if (isKts) {
        append("    androidTestImplementation(platform(libs.$name))\n")
    } else {
        append("    androidTestImplementation platform(libs.$name)\n")
    }
}

fun StringBuilder.addGradleDebugImplementation(library: Library, isKts: Boolean = true) {
    val name = library.libName.replace("-", ".")
    if (isKts) {
        append("    debugImplementation(libs.$name)\n")
    } else {
        append("    debugImplementation libs.$name\n")
    }
}

fun StringBuilder.addKspImplementation(library: Library, isKts: Boolean = true) {
    val name = library.libName.replace("-", ".")
    if (isKts) {
        append("    ksp(libs.$name)\n")
    } else {
        append("    ksp libs.$name\n")
    }
}

fun StringBuilder.addDetektBlock() {
    append("\ndetekt {\n")
    append("    config.setFrom(file(\"\$rootDir/detekt/detektConfig.yml\"))\n")
    append("    source.from(files(\"src/main/kotlin\"))\n")
    append("    parallel = true\n")
    append("    autoCorrect = true\n")
    append("    buildUponDefaultConfig = true\n")
    append("}\n")
}

fun StringBuilder.addAndroidBlock(
    packageName: String,
    minApi: Int,
    javaJvmVersion: String,
    isCompose: Boolean,
    isKts: Boolean = true
) {
    val assignment = if (isKts) " = " else " "
    append("android {\n")
    append("    namespace${assignment}\"${packageName}\"\n")
    append("    compileSdk${assignment}36\n\n")
    if (isKts) {
        append("    android.buildFeatures.buildConfig = true\n\n")
    } else {
        append("    buildFeatures {\n")
        append("        buildConfig true\n")
        append("    }\n\n")
    }
    append("    defaultConfig {\n")
    append("        applicationId${assignment}\"${packageName}\"\n")
    append("        minSdk${assignment}$minApi\n")
    append("        targetSdk${assignment}36\n")
    append("        versionCode${assignment}1\n")
    append("        versionName${assignment}\"1.0\"\n\n")
    append("        testInstrumentationRunner${assignment}\"androidx.test.runner.AndroidJUnitRunner\"\n")
    if (isKts) {
        append("        vectorDrawables.useSupportLibrary = true\n")
    } else {
        append("        vectorDrawables.useSupportLibrary true\n")
    }
    append("    }\n\n")
    append("    buildTypes {\n")
    append("        release {\n")
    append("            isMinifyEnabled${assignment}false\n")
    append("            proguardFiles(\n")
    append("                getDefaultProguardFile(\"proguard-android-optimize.txt\"),\n")
    append("                \"proguard-rules.pro\"\n")
    append("            )\n")
    append("        }\n")
    append("    }\n")
    append("    compileOptions {\n")
    append("        sourceCompatibility${assignment}JavaVersion.VERSION_$javaJvmVersion\n")
    append("        targetCompatibility${assignment}JavaVersion.VERSION_$javaJvmVersion\n")
    append("    }\n")
    if (isKts) {
        append("    kotlin {\n")
        append("        compilerOptions.jvmTarget.set(JvmTarget.JVM_$javaJvmVersion)\n")
        append("    }\n")
    } else {
        append("    kotlinOptions {\n")
        append("        jvmTarget = '$javaJvmVersion'\n")
        append("    }\n")
    }
    append("    buildFeatures {\n")
    if (isCompose) append("        compose${assignment}true\n") else append("        viewBinding${assignment}true\n")
    append("    }\n")
    append("    packaging {\n")
    append("        resources {\n")
    append("            excludes += \"/META-INF/{AL2.0,LGPL2.1}\"\n")
    append("        }\n")
    append("    }\n")
    append("}\n\n")
}

fun getImage(className: String, imagePath: String): Thumb {
    val pluginClassLoader =
        Class.forName("com.github.cnrture.quickprojectwizard.projectwizard.${className}Kt").classLoader
    val imageUrl = pluginClassLoader?.getResource("images/$imagePath.png")
    return if (imageUrl != null) {
        Thumb { imageUrl }
    } else {
        Thumb { URI("https://canerture.com/$imagePath.png").toURL() }
    }
}
