package com.rvv.android.sample.purchasing.domain

data class License(val premium: Premium)

enum class Premium { No, Yes, NotInitialized }

data class GoogleLicenseDetails(val month: LicensePrice, val year: LicensePrice)

sealed class LicensePrice(val microPrice: Long, val currencyCode: String)
class GoogleLicensePrice(price: Long, code: String) : LicensePrice(price, code)

data class DiscountGoogleLicensePrice(
    private val oldPrice: GoogleLicensePrice,
    val newPrice: GoogleLicensePrice,
    val period: Int? = null,
) : LicensePrice(oldPrice.microPrice, oldPrice.currencyCode)
