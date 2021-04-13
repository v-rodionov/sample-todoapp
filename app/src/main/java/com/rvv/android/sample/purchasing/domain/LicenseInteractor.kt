package com.rvv.android.sample.purchasing.domain

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

interface LicenseInteractor {

    fun getLicenseStateFlow(): Flow<License>

    fun isPremium(): Boolean

    suspend fun getDetails(): GoogleLicenseDetails

    suspend fun checkTrialAvailable(): Boolean

    suspend fun buyMonth()

    suspend fun buyYear()
}

class LicenseInteractorImpl
@Inject constructor(
) : LicenseInteractor {

    private val licenseState = MutableStateFlow(License(Premium.NotInitialized))

    override fun getLicenseStateFlow(): Flow<License> = licenseState

    override fun isPremium(): Boolean = false

    override suspend fun getDetails(): GoogleLicenseDetails {
        delay(500)
        val month = GoogleLicensePrice(50000000, "rub")
        val year = DiscountGoogleLicensePrice(GoogleLicensePrice(1499000000, "rub"), GoogleLicensePrice(499000000, "rub"))

        return GoogleLicenseDetails(month, year)
    }

    override suspend fun checkTrialAvailable() = false

    override suspend fun buyMonth() {
        delay(500)
        error("Not yet implemented")
    }

    override suspend fun buyYear() {
        delay(500)
        error("Not yet implemented")
    }
}
