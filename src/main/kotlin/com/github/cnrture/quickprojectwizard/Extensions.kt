package com.github.cnrture.quickprojectwizard

import com.android.tools.idea.wizard.template.ModuleTemplateData
import com.android.tools.idea.wizard.template.RecipeExecutor

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

fun StringBuilder.addLibsVersion(name: String, versionRef: String) {
    append("$name = \"$versionRef\"\n")
}

fun StringBuilder.addLibsDependency(libName: String, group: String, name: String, versionRef: String? = null) {
    val version = if (versionRef == null) "" else ", version.ref = \"$versionRef\""
    append("$libName = { group = \"$group\", name = \"$name\"$version }\n")
}

fun StringBuilder.addLibsPlugin(name: String, id: String, versionRef: String) {
    append("$name = { id = \"$id\", version.ref = \"$versionRef\" }\n")
}

fun StringBuilder.addGradlePlugin(name: String, isProject: Boolean = false) {
    val lastPath = if (isProject) " apply false" else ""
    append("    alias(libs.plugins.$name)$lastPath\n")
}

fun StringBuilder.addGradleImplementation(name: String) {
    append("    implementation(libs.$name)\n")
}

fun StringBuilder.addGradleDetektImplementation(name: String) {
    append("    detektPlugins(libs.$name)\n")
}

fun StringBuilder.addGradlePlatformImplementation(name: String) {
    append("    implementation(platform(libs.$name))\n")
}

fun StringBuilder.addGradleTestImplementation(name: String) {
    append("    testImplementation(libs.$name)\n")
}

fun StringBuilder.addGradleAndroidTestImplementation(name: String) {
    append("    androidTestImplementation(libs.$name)\n")
}
fun StringBuilder.addGradleAndroidTestPlatformImplementation(name: String) {
    append("    androidTestImplementation(platform(libs.$name))\n")
}

fun StringBuilder.addGradleDebugImplementation(name: String) {
    append("    debugImplementation(libs.$name)\n")
}

fun StringBuilder.addKspImplementation(name: String) {
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
