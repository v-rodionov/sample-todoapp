object App {
    const val minSdk = 21
    const val targetSdk = 30
    const val versionCode = 148
    val versionName
        get(): String {
            val startTrVersionCode = 147
            val lastNum = versionCode - startTrVersionCode
            return "1.22.$lastNum"
        }
}

object Versions {
    const val detekt = "1.9.0"
    const val stetho = "1.5.1"
    const val cicerone = "3.0.0"
    const val dagger = "2.33"
    const val picasso = "2.8"
    const val gson = "2.8.6"
    const val timber = "4.7.1"
    const val material = "1.3.0"

    object AndroidX {
        const val appCompat = "1.2.0"
        const val recycler_selection = "1.1.0"
        const val arch = "2.3.1"
        const val constraint = "2.0.4"
        const val ktx = "1.3.2"
        const val room = "2.2.6"
        const val work_manager = "2.5.0"
        const val preference = "1.1.1"
    }

    object Kotlin {
        const val kotlin = "1.4.32"
        const val coroutines = "1.4.3"
    }

    object Firebase {
        const val analytics = "18.0.2"
        const val authentication = "20.0.3"
        const val firestore = "22.1.2"
        const val messaging = "21.0.1"
        const val storage = "19.2.2"
        const val crashlytics = "17.4.1"
        const val remoteConfig = "20.0.4"
    }

    object GPlay {
        const val auth = "19.0.0"
        const val billing = "3.0.0"
        const val core = "1.8.1"
    }

    object Test {
        const val junit = "4.12"
        const val mockk = "1.9.3"
        const val runner = "1.1.1"
        const val orchestrator = "1.2.0"
        const val kaspresso = "1.0.1"
        const val arch_testing = "2.1.0"
    }
}
