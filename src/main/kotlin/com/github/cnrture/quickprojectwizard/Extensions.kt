package com.github.cnrture.quickprojectwizard

import com.android.tools.idea.wizard.template.ModuleTemplateData
import com.android.tools.idea.wizard.template.RecipeExecutor
import com.github.cnrture.quickprojectwizard.gradle.Library
import com.github.cnrture.quickprojectwizard.gradle.Plugin
import com.github.cnrture.quickprojectwizard.gradle.Version

fun RecipeExecutor.addRootFile(
    data: String,
    moduleData: ModuleTemplateData,
    dirPath: String,
) {
    save(data, moduleData.rootDir.parentFile.resolve(dirPath))
}

fun RecipeExecutor.addSrcFile(
    data: String,
    moduleData: ModuleTemplateData,
    filePath: String,
) {
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

fun StringBuilder.addGradlePlugin(plugin: Plugin, isProject: Boolean = false) {
    val name = plugin.name.replace("-", ".")
    val lastPath = if (isProject) " apply false" else ""
    append("    alias(libs.plugins.$name)$lastPath\n")
}

fun StringBuilder.addGradleImplementation(library: Library) {
    val name = library.libName.replace("-", ".")
    append("    implementation(libs.$name)\n")
}

fun StringBuilder.addGradleDetektImplementation(library: Library) {
    val name = library.libName.replace("-", ".")
    append("    detektPlugins(libs.$name)\n")
}

fun StringBuilder.addGradlePlatformImplementation(library: Library) {
    val name = library.libName.replace("-", ".")
    append("    implementation(platform(libs.$name))\n")
}

fun StringBuilder.addGradleTestImplementation(library: Library) {
    val name = library.libName.replace("-", ".")
    append("    testImplementation(libs.$name)\n")
}

fun StringBuilder.addGradleAndroidTestImplementation(library: Library) {
    val name = library.libName.replace("-", ".")
    append("    androidTestImplementation(libs.$name)\n")
}

fun StringBuilder.addGradleAndroidTestPlatformImplementation(library: Library) {
    val name = library.libName.replace("-", ".")
    append("    androidTestImplementation(platform(libs.$name))\n")
}

fun StringBuilder.addGradleDebugImplementation(library: Library) {
    val name = library.libName.replace("-", ".")
    append("    debugImplementation(libs.$name)\n")
}

fun StringBuilder.addKspImplementation(library: Library) {
    val name = library.libName.replace("-", ".")
    append("    ksp(libs.$name)\n")
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

fun StringBuilder.addAndroidBlock(packageName: String, minApi: Int, javaJvmVersion: String, isCompose: Boolean) {
    append("android {\n")
    append("    namespace = \"${packageName}\"\n")
    append("    compileSdk = 34\n\n")
    append("    android.buildFeatures.buildConfig = true\n\n")
    append("    defaultConfig {\n")
    append("        applicationId = \"${packageName}\"\n")
    append("        minSdk = $minApi\n")
    append("        targetSdk = 34\n")
    append("        versionCode = 1\n")
    append("        versionName = \"1.0\"\n\n")
    append("        testInstrumentationRunner = \"androidx.test.runner.AndroidJUnitRunner\"\n")
    append("        vectorDrawables.useSupportLibrary = true\n")
    append("    }\n\n")
    append("    buildTypes {\n")
    append("        release {\n")
    append("            isMinifyEnabled = false\n")
    append("            proguardFiles(\n")
    append("                getDefaultProguardFile(\"proguard-android-optimize.txt\"),\n")
    append("                \"proguard-rules.pro\"\n")
    append("            )\n")
    append("        }\n")
    append("    }\n")
    append("    compileOptions {\n")
    append("        sourceCompatibility = JavaVersion.VERSION_$javaJvmVersion\n")
    append("        targetCompatibility = JavaVersion.VERSION_$javaJvmVersion\n")
    append("    }\n")
    append("    kotlinOptions {\n")
    append("        jvmTarget = \"$javaJvmVersion\"\n")
    append("    }\n")
    append("    buildFeatures {\n")
    if (isCompose) append("        compose = true\n") else append("        viewBinding = true\n")
    append("    }\n")
    append("    packaging {\n")
    append("        resources {\n")
    append("            excludes += \"/META-INF/{AL2.0,LGPL2.1}\"\n")
    append("        }\n")
    append("    }\n")
    append("}\n\n")
}
