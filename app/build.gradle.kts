plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "br.com.entrevizinhos"
    compileSdk = 36

    defaultConfig {
        applicationId = "br.com.entrevizinhos"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildFeatures {
            viewBinding = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Navegação (Fragment e UI)
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7") // ou versão mais recente
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // firebase
    implementation(platform("com.google.firebase:firebase-bom:34.6.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore")
    // --- FIREBASE (Essencial para o AuthRepository) ---
    // Importa a plataforma Firebase (BoM) - gerencia versões automaticamente
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))

    // Autenticação (Login)
    implementation("com.google.firebase:firebase-auth")

    // Banco de Dados (Firestore)
    implementation("com.google.firebase:firebase-firestore")

    // Armazenamento de Imagens (Storage) - se for usar foto de perfil
    implementation("com.google.firebase:firebase-storage")

    // --- IMAGENS (Glide) ---
    // Para carregar a foto do perfil redonda
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // --- ARQUITETURA MVVM ---
    // ViewModel e LiveData (Para criar o PerfilViewModel)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.0")

    // Fragment KTX (Para usar 'by viewModels()')
    implementation("androidx.fragment:fragment-ktx:1.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
    // google login
    implementation("com.google.android.gms:play-services-auth:21.0.0")
}
