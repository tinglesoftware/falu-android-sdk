plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-parcelize")
}

android {
    compileSdk = 33
    namespace = project.properties["FALU_SDK_NAMESPACE"].toString()

    defaultConfig {
        minSdk = 23

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    sourceSets {
        getByName("main") {
            res.srcDirs(listOf("res"))
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    kotlin {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of("17"))
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    lint {
        abortOnError = false
    }
}

dependencies {
    api(libs.abstractions)
    implementation(libs.kotlin)

    testImplementation(libs.junit)
    testImplementation(libs.bundles.mokito)
    testImplementation(libs.nhaarman)
    testImplementation(libs.mockwebserver)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.robo)
    testImplementation(libs.mokito.kotlin)
    testImplementation(libs.bundles.kotlin.test)
}

ext {
    set("artifactId", "falu-core")
    set("artifactName", "Falu Core")
    set("artifactDescription", "Common packages for Falu Android")
}

apply(from = "${rootDir}/deploy/deploy.gradle")