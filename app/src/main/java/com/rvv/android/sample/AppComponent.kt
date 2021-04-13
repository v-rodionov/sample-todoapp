package com.rvv.android.sample

import android.app.Application
import android.content.Context
import com.rvv.android.sample.purchasing.domain.LicenseInteractor
import com.rvv.android.sample.purchasing.domain.LicenseInteractorImpl
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
    ]
)
interface AppComponent {

    fun inject(app: App)

    fun mainComponent(): MainComponent

    @Component.Builder
    interface Builder {
        fun appModule(appModule: AppModule): Builder
        fun build(): AppComponent
    }
}

@Module(includes = [AppModuleBindings::class])
class AppModule(private val app: App) {

    private val cicerone = Cicerone.create()

    @Provides
    @Singleton
    fun app(): App = app

    @Provides
    @Singleton
    fun application(): Application = app

    @Provides
    @Singleton
    @Deprecated("should use application instead raw context")
    fun context(): Context = app

    @Provides
    @Singleton
    fun router(): Router = cicerone.router

    @Provides
    @Singleton
    fun navigatorHolder(): NavigatorHolder = cicerone.navigatorHolder
}

@Module
interface AppModuleBindings {

    @Binds
    @Singleton
    fun licenseInteractor(s: LicenseInteractorImpl): LicenseInteractor
}
