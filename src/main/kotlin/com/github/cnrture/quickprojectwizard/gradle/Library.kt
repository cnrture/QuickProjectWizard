package com.github.cnrture.quickprojectwizard.gradle

sealed class Library(val libName: String, val group: String, val name: String, val verRef: String? = null) {
    data object CoreKtx : Library("core-ktx", "androidx.core", "core-ktx", "core-ktx")
    data object Junit : Library("junit", "junit", "junit", "junit")
    data object JunitExt : Library("junit-ext", "androidx.test.ext", "junit", "junit-ext")
    data object EspressoCore : Library("espresso-core", "androidx.test.espresso", "espresso-core", "espresso-core")
    data object AppCompat : Library("appcompat", "androidx.appcompat", "appcompat", "appcompat")
    data object Material : Library("material", "com.google.android.material", "material", "material")
    data object LifecycleRuntimeKtx :
        Library("lifecycle-runtime-ktx", "androidx.lifecycle", "lifecycle-runtime-ktx", "lifecycle-runtime-ktx")
    data object FragmentKtx : Library("fragment-ktx", "androidx.fragment", "fragment-ktx", "fragment-ktx")
    data object LifecycleRuntimeCompose :
        Library("lifecycle-runtime-compose", "androidx.lifecycle", "lifecycle-runtime-compose", "lifecycle-runtime-ktx")

    data object ActivityCompose :
        Library("activity-compose", "androidx.activity", "activity-compose", "activity-compose")

    data object ComposeBom : Library("compose-bom", "androidx.compose", "compose-bom", "compose-bom")
    data object ComposeUi : Library("compose-ui", "androidx.compose.ui", "ui")
    data object ComposeUiGraphics : Library("compose-ui-graphics", "androidx.compose.ui", "ui-graphics")
    data object ComposeUiToolingPreview :
        Library("compose-ui-tooling-preview", "androidx.compose.ui", "ui-tooling-preview")

    data object Material3 : Library("material3", "androidx.compose.material3", "material3")
    data object ComposeUiTooling : Library("compose-ui-tooling", "androidx.compose.ui", "ui-tooling")
    data object ComposeUiTestManifest : Library("compose-ui-test-manifest", "androidx.compose.ui", "ui-test-manifest")
    data object ComposeUiTestJunit4 : Library("compose-ui-test-junit4", "androidx.compose.ui", "ui-test-junit4")
    data object Activity : Library("activity", "androidx.activity", "activity", "activity")
    data object ConstraintLayout :
        Library("constraintlayout", "androidx.constraintlayout", "constraintlayout", "constraintlayout")

    data object Coil : Library("coil", "io.coil-kt", "coil", "coilVersion")
    data object CoilCompose : Library("coil-compose", "io.coil-kt", "coil-compose", "coilVersion")
    data object Glide : Library("glide", "com.github.bumptech.glide", "glide", "glideVersion")
    data object GlideCompose : Library("glide-compose", "com.github.bumptech.glide", "compose", "glideCompose")
    data object RoomKtx : Library("room-ktx", "androidx.room", "room-ktx", "room")
    data object RoomRuntime : Library("room-runtime", "androidx.room", "room-runtime", "room")
    data object RoomCompiler : Library("room-compiler", "androidx.room", "room-compiler", "room")
    data object Retrofit : Library("retrofit", "com.squareup.retrofit2", "retrofit", "retrofit")
    data object ConverterGson : Library("converter-gson", "com.squareup.retrofit2", "converter-gson", "retrofit")
    data object KtorClientCore : Library("ktor-client-core", "io.ktor", "ktor-client-core", "ktor")
    data object KtorClientOkHttp : Library("ktor-client-okhttp", "io.ktor", "ktor-client-okhttp", "ktor")
    data object KtorContentNegotiation :
        Library("ktor-client-content-negotiation", "io.ktor", "ktor-client-content-negotiation", "ktor")

    data object KtorSerialization :
        Library("ktor-serialization-kotlinx-json", "io.ktor", "ktor-serialization-kotlinx-json", "ktor")

    data object HiltAndroid : Library("hilt-android", "com.google.dagger", "hilt-android", "hilt")
    data object HiltCompiler : Library("hilt-compiler", "com.google.dagger", "hilt-compiler", "hilt")
    data object HiltNavigationCompose :
        Library("hilt-navigation-compose", "androidx.hilt", "hilt-navigation-compose", "hiltNavigationCompose")

    data object NavigationCompose :
        Library("navigation-compose", "androidx.navigation", "navigation-compose", "navigation")

    data object NavigationFragment :
        Library("navigation-fragment", "androidx.navigation", "navigation-fragment-ktx", "navigation")
    data object NavigationUi : Library("navigation-ui", "androidx.navigation", "navigation-ui-ktx", "navigation")
    data object Detekt : Library("detekt-formatting", "io.gitlab.arturbosch", "detekt-formatting", "detekt")
    data object Firebase : Library("firebase", "com.google.firebase", "firebase-bom", "firebase")
    data object WorkManager : Library("workManager", "androidx.work", "work-runtime-ktx", "workManagerVersion")
    data object KotlinxSerialization : Library("kotlinx-serialization", "org.jetbrains.kotlinx", "kotlinx-serialization-json", "kotlinxSerialization")
}