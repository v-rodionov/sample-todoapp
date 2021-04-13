package com.rvv.android.sample.mvvm

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope

abstract class BaseViewModel : ViewModel(), LifecycleObserver {

    protected val mainScope: CoroutineScope = viewModelScope
}
