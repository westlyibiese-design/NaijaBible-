plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

// Signing details come from environment variables so the keystore
// is never inside the project and never committed.
val signingKeystorePath: String? = System.getenv("NAIJABIBLE_KEYSTORE_PATH")
val signingKeystorePassword: String? = System.getenv("NAIJABIBLE_KEYSTORE_PASSWORD")
val signingKeyAlias: String? = System.getenv("NAIJABIBLE_KEY_ALIAS")
val signingKeyPassword: String? = System.getenv("NAIJABIBLE_KEY_PASSWORD")

val hasSigning: Boolean =
    !signingKeystorePath.isNullOrBlank() &&
        !signingKeystorePassword.isNullOrBlank() &&
        !signingKeyAlias.isNullOrBlank() &&
        !signingKeyPassword.isNullOrBlank() &&
        file(signingKeystorePath!!).exists()

android {
    namespace = "com.westly.naijabible"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.westly.naijabible"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    signingConfigs {
        if (hasSigning) {
            create("release") {
                storeFile = file(signingKeystorePath!!)
                storePassword = signingKeystorePassword
                keyAlias = signingKeyAlias
                keyPassword = signingKeyPassword
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            if (hasSigning) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.navigation.compose)
}
