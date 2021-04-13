package com.rvv.android.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rvv.android.sample.purchasing.PurchaseFragment
import com.rvv.android.sample.purchasing.di.PurchaseComponentProvider
import dagger.Module
import dagger.Subcomponent

class MainActivity : AppCompatActivity(), PurchaseComponentProvider {

    private val component by lazy { app.appComponent.mainComponent() }

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, PurchaseFragment())
                .commit()
        }
    }

    override fun providePurchaseComponent() = component.providePurchaseComponent()
}

@ActivityScope
@Subcomponent(modules = [MainModule::class])
interface MainComponent : PurchaseComponentProvider {
    fun inject(activity: MainActivity)
}

@Module
interface MainModule