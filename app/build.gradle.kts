/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("plugin.serialization")
    id("com.mikepenz.aboutlibraries.plugin")
}

android {
    namespace = "me.zhanghai.android.untracker"
    buildToolsVersion = "34.0.0"
    compileSdk = 34

    defaultConfig {
        applicationId = "me.zhanghai.android.untracker"
        minSdk = 21
        targetSdk = 34
        versionCode = 5
        versionName = "1.0.4"

        vectorDrawables { useSupportLibrary = true }
    }

    signingConfigs {
        create("release") {
            storeFile = System.getenv("STORE_FILE")?.let { rootProject.file(it) }
            storePassword = System.getenv("STORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions { jvmTarget = JavaVersion.VERSION_1_8.toString() }
    buildFeatures { compose = true }
    composeOptions { kotlinCompilerExtensionVersion = "1.5.9" }
    packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
    bundle {
        language {
            enableSplit = false
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    implementation(platform("androidx.compose:compose-bom:2024.05.00"))
    implementation("androidx.compose.animation:animation-graphics")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    implementation("androidx.navigation:navigation-compose:2.7.7")

    implementation("com.github.zhanghai.quickjs-java:quickjs-android:0.9.3")
    implementation("com.mikepenz:aboutlibraries-core:10.9.2")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("me.zhanghai.compose.preference:library:1.0.0")
}
