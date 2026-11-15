import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

// La URL del backend se resuelve en este orden: local.properties, gradle.properties.
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use(::load)
}
val backendUrl: String = localProperties.getProperty("postura.backendUrl")
    ?: providers.gradleProperty("postura.backendUrl").get()

android {
    namespace = "com.posturawear.core"
    compileSdk = 37

    defaultConfig {
        minSdk = 26
        buildConfigField("String", "BACKEND_URL", "\"$backendUrl\"")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    api(platform(libs.compose.bom))
    api(libs.compose.ui)
    api(libs.compose.ui.graphics)
    api(libs.compose.foundation)
    api(libs.compose.ui.tooling.preview)
    api(libs.wear.compose.material3)
    api(libs.wear.compose.foundation)
    api(libs.wear.compose.navigation)
    api(libs.activity.compose)
    api(libs.lifecycle.viewmodel.compose)
    api(libs.lifecycle.runtime.compose)
    api(libs.core.ktx)
    api(libs.coroutines.core)
    implementation(libs.coroutines.android)
    implementation(libs.datastore.preferences)
    implementation(libs.serialization.json)

    debugImplementation(libs.compose.ui.tooling)

    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)
}
