plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.gms.google-services")

}

android {

    compileSdk = 35
    defaultConfig {
        applicationId = "com.example.moso"
        minSdk = 23
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    // Aquí es donde se debe especificar el `namespace` que corresponde al nombre del paquete de la aplicación
    namespace = "com.example.moso"  // Añade esta línea para resolver el error de namespace
}

configurations.all {
    resolutionStrategy {
        // Solo forzamos navegación; dejamos que el BOM de Firebase gestione sus propias versiones
        force("androidx.navigation:navigation-compose:2.9.0")
    }
}

dependencies {
    // Jetpack Compose
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Firebase dependencies
    implementation(platform(libs.firebase.bom)) // Firebase BOM to manage versions
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.firebase.database.ktx)

    // Navigation
    implementation(libs.androidx.navigation.compose.android)

    // Coil for image loading
    implementation(libs.coil.compose)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Firebase Auth ya lo tienes
    implementation ("com.google.android.gms:play-services-auth:21.3.0")      // Para Google Sign-In
    // Para Google Sign-In
    implementation("com.facebook.android:facebook-login:15.2.0")

    // Material Icons Extended …
    implementation("androidx.compose.material:material-icons-extended:1.4.4")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
}


