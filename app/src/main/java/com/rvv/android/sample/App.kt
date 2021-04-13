package com.rvv.android.sample

import android.app.Application

open class App : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = createAppComponent()
        appComponent.inject(this)
    }

    private fun createAppComponent(): AppComponent {
        val builder = DaggerAppComponent.builder()
            .appModule(AppModule(this))

        return builder.build()
    }
}
