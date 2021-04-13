buildscript {
    repositories {
        google()
        jcenter()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:4.1.3")
        classpath("com.google.gms:google-services:4.3.5")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.5.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.Kotlin.kotlin}")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven(url = "https://jitpack.io")
        flatDir { dirs = setOf(file("app/libs")) }
    }
}

plugins {
    id("io.gitlab.arturbosch.detekt").version(Versions.detekt)
}

detekt {
    val analysisDir = "${project.rootDir}/app/analysis"
    val rulesDir = "$analysisDir/rules"

    autoCorrect = true
    toolVersion = Versions.detekt
    input = files("app")
    config = files("$rulesDir/detekt.yml")

    reports {
        xml { enabled = false }
        html { enabled = false }
        txt { enabled = false }
    }
}

dependencies {
    detekt("io.gitlab.arturbosch.detekt:detekt-cli:${Versions.detekt}")
    detekt("io.gitlab.arturbosch.detekt:detekt-core:${Versions.detekt}")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:${Versions.detekt}")
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
    delete(File("${project.rootDir}/app/release"))
}

