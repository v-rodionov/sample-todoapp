package com.rvv.android.sample.purchasing

import androidx.lifecycle.MutableLiveData
import com.rvv.android.sample.R
import com.rvv.android.sample.StringResource
import com.rvv.android.sample.mvvm.BaseViewModel
import com.rvv.android.sample.pluralStringResourceOf
import com.rvv.android.sample.purchasing.domain.DiscountGoogleLicensePrice
import com.rvv.android.sample.purchasing.domain.GoogleLicensePrice
import com.rvv.android.sample.purchasing.domain.License
import com.rvv.android.sample.purchasing.domain.LicenseInteractor
import com.rvv.android.sample.purchasing.domain.Premium
import com.rvv.android.sample.stringResourceOf
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router
import java.text.DecimalFormat
import java.util.Locale
import javax.inject.Inject
import kotlin.math.ceil

class PurchaseViewModel
@Inject constructor(
    private val licenseInteractor: LicenseInteractor,
    private val router: Router,
) : BaseViewModel() {

    companion object {
        private const val LOADING_DELAY = 666L
        private const val MICRO_MULTIPLIER = 1_000_000
        private const val MONTH_IN_YEAR = 12
    }

    sealed class State {
        object Loading : State()

        object Error : State()

        data class Data(
            val data: PriceData,
            val buyBtnText: StringResource,
            val yearPriceHint: StringResource,
        ) : State()
    }

    val state = MutableLiveData<State>()

    private var subscriptionPeriodIsMonth = false

    val isMonthSelected = MutableLiveData<Boolean>()
    val isYearSelected = MutableLiveData<Boolean>()

    init {
        mainScope.launch {
            licenseInteractor.getLicenseStateFlow().collect { license ->
                processPurchaseResult(license)
            }
        }

        loadInfo()
    }

    fun retry() {
        loadInfo()
    }

    fun buy() {
        mainScope.launch {
            state.value = State.Loading
            try {
                if (subscriptionPeriodIsMonth)
                    licenseInteractor.buyMonth()
                else
                    licenseInteractor.buyYear()
            } catch (exception: Exception) {
                state.value = State.Error
            }
        }
    }

    fun setSubscriptionPeriod(isMonth: Boolean) {
        this.subscriptionPeriodIsMonth = isMonth

        isMonthSelected.value = subscriptionPeriodIsMonth
        isYearSelected.value = !subscriptionPeriodIsMonth
    }

    fun showPrivacyPolicy() {
        // router.navigateTo(Screen.PRIVACY_POLICY)
    }

    fun showTermsAndConditions() {
        // router.navigateTo(Screen.TERMS_AND_CONDITIONS)
    }

    fun action() {
        router.exit()
    }

    private fun loadInfo() {
        mainScope.launch {
            state.value = State.Loading

            try {
                val delay = async { delay(LOADING_DELAY) }
                val licenseDetailsRequest = async { runCatching { licenseInteractor.getDetails() } }

                delay.await()
                val licenseDetails = licenseDetailsRequest.await().getOrThrow()

                val priceFormat = DecimalFormat("##.##")
                val (monthDetails, yearDetails) = licenseDetails

                val (newPrice, monthPeriod) = when (monthDetails) {
                    is GoogleLicensePrice -> null to null
                    is DiscountGoogleLicensePrice -> {
                        monthDetails.newPrice to monthDetails.period
                    }
                }

                val currentMonthDetails = newPrice ?: monthDetails
                val monthCurrencyCode = getCurrencySign(currentMonthDetails.currencyCode)
                val monthMicroPrice = currentMonthDetails.microPrice
                val monthRawPrice = monthMicroPrice.toDouble() / MICRO_MULTIPLIER
                val monthNormalPrice = priceFormat.format(monthRawPrice)
                val monthPrice = "$monthNormalPrice $monthCurrencyCode"

                val crossedDetails = if (newPrice != null) monthDetails else null
                val crossedMonthPrice = crossedDetails?.let { details ->
                    val discountMonthCurrencyCode = getCurrencySign(details.currencyCode)
                    val rawPrice = details.microPrice.toDouble() / MICRO_MULTIPLIER
                    val normalPrice = priceFormat.format(rawPrice)
                    "$normalPrice $discountMonthCurrencyCode"
                }

                val (newYearPrice, _) = when (yearDetails) {
                    is GoogleLicensePrice -> null to null
                    is DiscountGoogleLicensePrice -> {
                        yearDetails.newPrice to yearDetails.period
                    }
                }

                val yearCurrentPrice = newYearPrice ?: yearDetails
                val yearCurrencyCode = getCurrencySign(yearCurrentPrice.currencyCode)
                val yearMicroPrice = yearCurrentPrice.microPrice
                val yearRawPrice = yearMicroPrice.toDouble() / MONTH_IN_YEAR / MICRO_MULTIPLIER

                val yearFormattedPrice = if (yearRawPrice > 10) {
                    ceil(yearRawPrice).toInt().toString()
                } else {
                    priceFormat.format(yearRawPrice)
                }

                val yearPrice = "$yearFormattedPrice $yearCurrencyCode*"
                val fullYearPrice = "${yearMicroPrice / MICRO_MULTIPLIER} $yearCurrencyCode"

                val crossedYearDetails = if (newYearPrice != null) yearDetails else null
                val crossedYearPrice = crossedYearDetails?.let { details ->
                    val discountYearCurrencyCode = getCurrencySign(details.currencyCode)
                    val rawPrice = details.microPrice.toDouble() / MONTH_IN_YEAR / MICRO_MULTIPLIER
                    val normalPrice = if (rawPrice > 10) {
                        ceil(rawPrice).toInt().toString()
                    } else {
                        priceFormat.format(rawPrice)
                    }
                    "$normalPrice $discountYearCurrencyCode"
                }

                val hasTrial = licenseInteractor.checkTrialAvailable()
                val text = if (hasTrial) R.string.purchase_try_trial else R.string.purchase_activate
                val discountPeriod = monthPeriod?.let {
                    pluralStringResourceOf(R.plurals.purchase_discount_month_period, it, it)
                }

                val monthPriceText = PriceText(monthPrice, crossedMonthPrice, discountPeriod)
                val yearPriceText = PriceText(yearPrice, crossedYearPrice, period = null)
                setSubscriptionPeriod(isMonth = newPrice != null)

                state.value = State.Data(
                    PriceData(monthPriceText, yearPriceText),
                    stringResourceOf(text),
                    stringResourceOf(R.string.purchase_per_year, fullYearPrice)
                )
            } catch (exception: Exception) {
                state.value = State.Error
            }
        }
    }

    private fun processPurchaseResult(license: License) {
        when (license.premium) {
            Premium.No, Premium.NotInitialized -> return
            Premium.Yes -> router.exit()
        }
    }
}

data class PriceText(val price: String, val crossedPrice: String?, val period: StringResource?)
data class PriceData(val monthPrice: PriceText, val yearPrice: PriceText)

fun getCurrencySign(currencyCode: String): String {
    return when (currencyCode.toLowerCase(Locale.US)) {
        "rub" -> "₽" // рубль
        "usd" -> "$" // доллар
        "eur" -> "€" // евро
        "uah" -> "₴" // гривна
        else -> currencyCode
    }
}
