plugins {
    id("com.android.application")
    id("kotlin-android")
}

// Helper extension to run shell commands
fun String.runCommand(workingDir: File): String =
    ProcessBuilder(*split(" ").toTypedArray())
        .directory(workingDir)
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)
        .start()
        .inputStream
        .bufferedReader()
        .readText()

// Git-related version info: OUTSIDE buildscript
val gitSha = "git rev-parse --short HEAD".runCommand(rootDir).trim()
val gitVersionName = "m" + "git rev-list --count HEAD".runCommand(rootDir).trim()
val gitVersionCode = 1

android {
    compileSdk = 35

    defaultConfig {
        applicationId = "at.ff.timekeeper"
        minSdk = 24 // Android 7.0
        targetSdk = 35
        versionCode = gitVersionCode
        versionName = gitVersionName
        buildConfigField("long", "TIMESTAMP", "${System.currentTimeMillis()}L")
        buildConfigField("String", "GIT_SHA", "\"$gitSha\"")
        multiDexEnabled = true
        setProperty("archivesBaseName", "${versionName}-${project.name}")
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "../proguard-rules.pro"
            )
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "../proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    packaging {
        jniLibs {
            excludes += setOf("META-INF/LICENSE*")
        }
        resources {
            excludes += setOf("META-INF/LICENSE*")
        }
    }

    namespace = "at.ff.timekeeper"

    lint {
        enable.add("Interoperability")
    }
}

dependencies {
    implementation(libs.multidex)
    annotationProcessor(libs.daggerCompiler)
    annotationProcessor(libs.daggerAndroidProcessor)
    annotationProcessor(libs.lifecycleCompiler)

    implementation(libs.appcompat)
    implementation(libs.gson)
    implementation(libs.timber)
    implementation(libs.lifecycleCommonJava8)
    implementation(libs.lifecycleExtensions)
    implementation(libs.dagger)
    implementation(libs.daggerAndroidSupport)
    implementation(libs.coreKtx)
    implementation(libs.preference)
    implementation(libs.material)

    // data
    implementation(libs.roomRuntime)
    annotationProcessor(libs.roomCompiler)

    // ble
    implementation(libs.scanner)
    implementation(libs.ble)
}
