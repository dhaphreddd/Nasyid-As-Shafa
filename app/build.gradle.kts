import java.util.Properties

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.nasyidasshafa"
    compileSdk = 34 // Menggunakan SDK 34 yang lebih umum dan stabil

    defaultConfig {
        applicationId = "com.example.nasyidasshafa"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Read Cloudinary credentials from local.properties
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(localPropertiesFile.inputStream())
        }

        // BuildConfig fields for Cloudinary
        buildConfigField("String", "CLOUDINARY_CLOUD_NAME", "\"${localProperties.getProperty("cloudinary.cloud_name", "")}\"")
        buildConfigField("String", "CLOUDINARY_API_KEY", "\"${localProperties.getProperty("cloudinary.api_key", "")}\"")
        buildConfigField("String", "CLOUDINARY_API_SECRET", "\"${localProperties.getProperty("cloudinary.api_secret", "")}\"")
    }

    buildFeatures {
        buildConfig = true
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
        // Atur ke versi 1.8 agar konsisten dengan banyak library Android
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    // Perbaikan: Mendefinisikan 'activity' secara eksplisit dengan versi stabil
    implementation("androidx.activity:activity:1.8.0")

    // Firebase (BOM memastikan semua library Firebase kompatibel)
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    // implementation("com.google.firebase:firebase-storage") // Kita non-aktifkan karena tidak jadi pakai

    // ViewModel & LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata:2.7.0")

    // ViewPager2
    implementation("androidx.viewpager2:viewpager2:1.1.0")

    // PhotoView untuk zoom gambar
    implementation("com.github.chrisbanes:PhotoView:2.3.0")

    // Glide untuk memuat gambar
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Cloudinary
    implementation("com.cloudinary:cloudinary-android:2.4.0")

    implementation("jp.wasabeef:glide-transformations:4.3.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
}
