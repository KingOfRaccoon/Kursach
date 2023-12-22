import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinx.serialization)

}

kotlin {
//    @OptIn(ExperimentalWasmDsl::class)
//    wasmJs {
//        moduleName = "composeApp"
//        browser {
//            commonWebpackConfig {
//                outputFileName = "composeApp.js"
//            }
//        }
//        binaries.executable()
//    }
    js {
        browser()
        binaries.executable()
    }
    
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    
    jvm("desktop")

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
//                implementation("io.ktor:ktor-client-cio:2.3.2")
                api(project(":shared"))
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.material3)
                implementation(libs.voyager.navigator)
                implementation(libs.voyager.koin)
                implementation("cafe.adriel.voyager:voyager-bottom-sheet-navigator:1.0.0")
                implementation("cafe.adriel.voyager:voyager-transitions:1.0.0")
                implementation(libs.composeImageLoader)
                implementation(libs.napier)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.insetsx)
                implementation(libs.ktor.core)
                @OptIn(ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
                implementation(libs.ktor.json)
                implementation(libs.ktor.logging)
                implementation(libs.ktor.negotiation)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.koin.core)
                implementation(libs.koin.compose)
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")

//                implementation("io.github.boguszpawlowski.composecalendar:composecalendar:1.1.1")
                implementation("io.github.epicarchitect:calendar-compose-basis:1.0.5")
                implementation("io.github.epicarchitect:calendar-compose-ranges:1.0.5") // includes basis
                implementation("io.github.epicarchitect:calendar-compose-pager:1.0.5") // includes basis
                implementation("io.github.epicarchitect:calendar-compose-datepicker:1.0.5") // includes pager + ranges

                // separate artifact with utilities for working with kotlinx-datetime
//                implementation("io.github.boguszpawlowski.composecalendar:kotlinx-datetime:1.1.1")

//                api(libs.androidx.ui.text.google.fonts)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val androidMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-cio:2.3.2")
                implementation(libs.androidx.appcompat)
                implementation(libs.androidx.activityCompose)
                implementation(libs.compose.uitooling)
                implementation(libs.kotlinx.coroutines.android)
                implementation(libs.ktor.client.okhttp)
                implementation(libs.koin.android)
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-cio:2.3.2")
                implementation(compose.desktop.common)
                implementation(compose.desktop.currentOs)
                implementation(libs.ktor.client.okhttp)
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(compose.html.core)
                implementation(devNpm("copy-webpack-plugin", "9.1.0"))
                implementation("io.ktor:ktor-client-js:2.3.2")
            }
        }

//        val nativeMain by getting {
//            dependencies {
//                implementation("io.ktor:ktor-client-cio:2.3.2")
//            }
//        }

        val iosX64Main by getting {
            dependencies{
                implementation("io.ktor:ktor-client-cio:2.3.2")
            }
        }
        val iosArm64Main by getting {
            dependencies{
                implementation("io.ktor:ktor-client-cio:2.3.2")
            }
        }
        val iosSimulatorArm64Main by getting {
            dependencies{
                implementation("io.ktor:ktor-client-cio:2.3.2")
            }
        }
//        val iosMain by getting {
//            dependencies {
//                implementation(libs.ktor.client.darwin)
////                implementation(libs.sqlDelight.driver.native)
//            }
//        }
    }
}

android {
    namespace = "ru.kingofraccoons.kursach"
    compileSdk = 34

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "ru.kingofraccoons.kursach"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    dependencies {
//        debugImplementation(libs.compose.ui.tooling)
    }
}
dependencies {
    implementation(project(mapOf("path" to ":shared")))
    implementation(project(mapOf("path" to ":shared")))
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "ru.kingofraccoons.kursach"
            packageVersion = "1.0.0"
        }
    }
}

compose.experimental {
    web.application {}
}