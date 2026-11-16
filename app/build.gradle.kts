plugins {
    id("com.android.application")
}

android {
    namespace = "ru.college.attendance"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "ru.college.attendance"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "0.1.0-sprint1"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = false
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")

    testImplementation("junit:junit:4.13.2")
}
