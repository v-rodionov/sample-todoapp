@file:Suppress("MaxLineLength")

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
    id("com.google.firebase.crashlytics")
}

android {
    compileSdkVersion(App.targetSdk)

    buildFeatures {
        dataBinding = true
    }

    defaultConfig {
        minSdkVersion(App.minSdk)
        targetSdkVersion(App.targetSdk)

        applicationId = "com.rvv.android.todoapp"
        versionCode = App.versionCode
        versionName = App.versionName

        // only "default" and "ru" resources
        resConfig("ru")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["clearPackageData"] = "true"

        testOptions {
            execution = "ANDROIDX_TEST_ORCHESTRATOR"
        }

        javaCompileOptions {
            annotationProcessorOptions {
                arguments(mapOf(
                    "room.schemaLocation" to "$projectDir/schemas",
                    "room.incremental" to "true",
                    "room.expandProjection" to "true"
                ))
            }
        }

//        TODO: fix it
//        sourceSets {
//            this.getByName("androidTest").assets.srcDirs += files("$projectDir/schemas")
//        }

        buildTypes {
            getByName("debug") {
                isDebuggable = true
                isMinifyEnabled = false
                proguardFiles(
                    getDefaultProguardFile("proguard-android.txt"),
                    "proguard-rules-debug.pro"
                )
                testProguardFile("proguard-rules-debug.pro")

                firebaseCrashlytics {
                    // отключаем загрузку маппинга в дебаге -> ускоряем билд
                    mappingFileUploadEnabled = false
                }
            }
        }

        packagingOptions {
            // Удаляем мета-информацию о kotlin-классах -> уменьшаем размер apk
            exclude("META-INF/*.kotlin_module")
            exclude("**.kotlin_builtins")
            exclude("**.kotlin_metadata")
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }

        kotlinOptions {
            jvmTarget = "1.8"
        }

        kapt {
            useBuildCache = true
            arguments {
                arg("dagger.formatGeneratedSource", "disabled")
            }
        }
    }

    /**
     * Workaround for making unit tests cacheable.
     * For more information see https://issuetracker.google.com/issues/115873047
     */
    testOptions {
        unitTests.apply {
            isIncludeAndroidResources = true
        }
    }
}

android.applicationVariants.all {
    outputs.all {
        this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
        outputFileName = "todoapp_v$versionName.apk"
    }
}

dependencies {
    implementation(fileTree(mapOf("include" to arrayOf("*.aar"), "dir" to "libs")))
    implementation(fileTree(mapOf("include" to arrayOf("*.jar"), "dir" to "libs")))

    // Tests
    testImplementation("junit:junit:${Versions.Test.junit}")
    testImplementation("io.mockk:mockk:${Versions.Test.mockk}")
    testImplementation("androidx.arch.core:core-testing:${Versions.Test.arch_testing}")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.Kotlin.coroutines}")

    androidTestImplementation("androidx.work:work-testing:${Versions.AndroidX.work_manager}")
    androidTestImplementation("androidx.room:room-testing:${Versions.AndroidX.room}")
    androidTestImplementation("androidx.test.ext:junit:${Versions.Test.runner}")
    androidTestUtil("androidx.test:orchestrator:${Versions.Test.orchestrator}")
    androidTestImplementation("com.kaspersky.android-components:kaspresso:${Versions.Test.kaspresso}")

    // AndroidX
    implementation("androidx.appcompat:appcompat:${Versions.AndroidX.appCompat}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.AndroidX.arch}")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:${Versions.AndroidX.arch}")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:${Versions.AndroidX.arch}")
    implementation("androidx.constraintlayout:constraintlayout:${Versions.AndroidX.constraint}")
    implementation("androidx.core:core-ktx:${Versions.AndroidX.ktx}")
    implementation("androidx.room:room-ktx:${Versions.AndroidX.room}")
    kapt("androidx.room:room-compiler:${Versions.AndroidX.room}")
    implementation("androidx.recyclerview:recyclerview-selection:${Versions.AndroidX.recycler_selection}")
    implementation("androidx.work:work-runtime-ktx:${Versions.AndroidX.work_manager}")
    implementation("androidx.preference:preference-ktx:${Versions.AndroidX.preference}")

    // Google Play
    implementation("com.android.billingclient:billing-ktx:${Versions.GPlay.billing}")
    implementation("com.google.android.gms:play-services-auth:${Versions.GPlay.auth}")
    implementation("com.google.android.play:core-ktx:${Versions.GPlay.core}")

    // Firebase
    implementation("com.google.firebase:firebase-analytics-ktx:${Versions.Firebase.analytics}")
    implementation("com.google.firebase:firebase-crashlytics:${Versions.Firebase.crashlytics}")
    implementation("com.google.firebase:firebase-storage-ktx:${Versions.Firebase.storage}")
    implementation("com.google.firebase:firebase-auth-ktx:${Versions.Firebase.authentication}")
    implementation("com.google.firebase:firebase-messaging:${Versions.Firebase.messaging}")
    implementation("com.google.firebase:firebase-firestore-ktx:${Versions.Firebase.firestore}")
    implementation("com.google.firebase:firebase-config-ktx:${Versions.Firebase.remoteConfig}")

    // to fix build, see https://stackoverflow.com/a/60492942/6190107
    implementation("com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.Kotlin.kotlin}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.Kotlin.coroutines}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.Kotlin.coroutines}")

    // Utils
    implementation("com.google.code.gson:gson:${Versions.gson}")
    implementation("com.google.dagger:dagger:${Versions.dagger}")
    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")

    implementation("com.squareup.picasso:picasso:${Versions.picasso}")
    implementation("ru.terrakok.cicerone:cicerone:${Versions.cicerone}")
    debugImplementation("com.facebook.stetho:stetho:${Versions.stetho}")
    implementation("net.yslibrary.keyboardvisibilityevent:keyboardvisibilityevent:3.0.0-RC2")

    implementation("com.jakewharton:process-phoenix:2.0.0")
    implementation("com.jakewharton.timber:timber:${Versions.timber}")

    // Ui
    implementation("com.google.android.material:material:${Versions.material}")
    implementation("ca.antonious:materialdaypicker:0.7.2")
    implementation("com.sergivonavi:materialbanner:1.2.0")
    implementation("me.saket:better-link-movement-method:2.2.0")
}
