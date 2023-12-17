import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
}

kotlin {
    macosX64 {
        binaries {
            executable {
                entryPoint = "main"
                freeCompilerArgs += listOf(
                    "-linker-option", "-framework", "-linker-option", "Metal",
                )
            }
        }
    }
    macosArm64 {
        binaries {
            executable {
                entryPoint = "main"
                freeCompilerArgs += listOf(
                    "-linker-option", "-framework", "-linker-option", "Metal",
                )
            }
        }
    }
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

    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

//    iosX64()
//    iosArm64()
//    iosSimulatorArm64()

//    jvm()

    sourceSets {
        commonMain.dependencies {
            val ktorVersion = "2.3.2"
            implementation(libs.kotlinx.coroutines.core)
//            implementation("io.github.xxfast:decompose-router:0.5.1")

            // You will need to also bring in decompose and essenty
//            implementation("com.arkivanov.decompose:decompose:2.2.0")
//            implementation("com.arkivanov.decompose:extensions-compose-jetbrains:v0.5.1")
//            implementation("com.arkivanov.essenty:parcelable:v0.5.1")
            // put your Multiplatform dependencies here
//            implementation(libs.precompose)
            implementation("io.insert-koin:koin-core:3.5.0")
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
            implementation("io.ktor:ktor-client-core:$ktorVersion")
//            implementation("io.ktor:ktor-client-cio:2.3.2")
        }
    }
}

android {
    namespace = "ru.kingofraccoons.kursach.shared"
    compileSdk = 34
    defaultConfig {
        minSdk = 21
    }
}
