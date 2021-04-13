package com.rvv.android.sample.purchasing.di

import androidx.lifecycle.ViewModel
import com.rvv.android.sample.FragmentScope
import com.rvv.android.sample.mvvm.ViewModelKey
import com.rvv.android.sample.purchasing.PurchaseFragment
import com.rvv.android.sample.purchasing.PurchaseViewModel
import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import dagger.multibindings.IntoMap

@FragmentScope
@Subcomponent(modules = [PurchaseComponentModule::class])
interface PurchaseComponent {
    fun inject(fragment: PurchaseFragment)
}

@Module
interface PurchaseComponentModule {

    @Binds
    @IntoMap
    @ViewModelKey(PurchaseViewModel::class)
    fun purchaseViewModel(p: PurchaseViewModel): ViewModel
}

interface PurchaseComponentProvider {
    fun providePurchaseComponent(): PurchaseComponent
}
