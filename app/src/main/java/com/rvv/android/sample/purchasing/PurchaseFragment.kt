package com.rvv.android.sample.purchasing

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.rvv.android.sample.PriceTagViewData
import com.rvv.android.sample.R
import com.rvv.android.sample.getComponentProvider
import com.rvv.android.sample.getViewModel
import com.rvv.android.sample.purchasing.di.PurchaseComponent
import com.rvv.android.sample.purchasing.di.PurchaseComponentProvider
import com.rvv.android.sample.setLinks
import com.rvv.android.sample.setResource
import com.rvv.android.sample.text
import com.rvv.android.sample.mvvm.ViewModelFactory
import kotlinx.android.synthetic.main.purchase.purchase_buy_btn
import kotlinx.android.synthetic.main.purchase.purchase_hint
import kotlinx.android.synthetic.main.purchase.purchase_price_tag_container
import kotlinx.android.synthetic.main.purchase.purchase_price_tag_month
import kotlinx.android.synthetic.main.purchase.purchase_price_tag_year
import kotlinx.android.synthetic.main.purchase.purchase_purchase_progress
import kotlinx.android.synthetic.main.purchase.purchase_repeat_btn
import kotlinx.android.synthetic.main.purchase.purchase_repeat_txt
import kotlinx.android.synthetic.main.purchase.purchase_year_hint
import javax.inject.Inject

class PurchaseFragment : Fragment(R.layout.purchase) {

    @Inject lateinit var vmFactory: ViewModelFactory
    private lateinit var viewModel: PurchaseViewModel
    private lateinit var component: PurchaseComponent

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val componentProvider: PurchaseComponentProvider = getComponentProvider()
        component = componentProvider.providePurchaseComponent().also { it.inject(this) }
    }

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        viewModel = getViewModel(vmFactory)
        lifecycle.addObserver(viewModel)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val lifecycleOwner = viewLifecycleOwner

        val privacyPolicy = getString(R.string.purchase_hint_privacy_policy)
        val termsAndConditions = getString(R.string.purchase_hint_eula)

        purchase_hint.setLinks(
            listOf(
                termsAndConditions to {
                    viewModel.showTermsAndConditions()
                    Toast.makeText(requireContext(), "showTermsAndConditions", Toast.LENGTH_SHORT).show()
                },
                privacyPolicy to {
                    viewModel.showPrivacyPolicy()
                    Toast.makeText(requireContext(), "showPrivacyPolicy", Toast.LENGTH_SHORT).show()
                }
            ), R.string.purchase_hint, R.color.colorAccent
        )

        purchase_price_tag_month.setOnClickListener {
            viewModel.setSubscriptionPeriod(isMonth = true)
        }

        purchase_price_tag_year.setOnClickListener {
            viewModel.setSubscriptionPeriod(isMonth = false)
        }

        purchase_buy_btn.setOnClickListener { viewModel.buy() }

        purchase_repeat_btn.setOnClickListener {
            changeErrorVisibility(needShow = false)
            viewModel.retry()
        }

        viewModel.state.observe(lifecycleOwner) { state ->
            when (state) {
                is PurchaseViewModel.State.Data -> {
                    val context = requireContext()

                    val (monthPriceText, yearPriceText) = state.data
                    val (monthPrice, crossedMonthPrice) = monthPriceText
                    val (yearPrice, crossedYearPrice) = yearPriceText

                    val monthPlanName = context.getString(R.string.purchase_plan_name_month)
                    val monthPerPeriodText = context.getString(R.string.purchase_per_month)
                    val monthPlanPayPeriod =
                        context.getString(R.string.purchase_plan_pay_period_month)

                    val yearPlanName = context.getString(R.string.purchase_plan_name_year)
                    val yearPlanPayPeriod =
                        context.getString(R.string.purchase_plan_pay_period_year)

                    val hasDiscount = monthPriceText.period != null
                    val monthAdditional =
                        if (hasDiscount) monthPriceText.period.text(context) else null
                    val yearAdditional =
                        if (hasDiscount) yearPriceText.period.text(context) else null

                    val monthData = PriceTagViewData(
                        monthPrice, crossedMonthPrice, monthPlanPayPeriod,
                        monthPlanName, monthPerPeriodText, monthAdditional
                    )

                    val yearData = PriceTagViewData(
                        yearPrice, crossedYearPrice, yearPlanPayPeriod,
                        yearPlanName, monthPerPeriodText, yearAdditional
                    )

                    purchase_price_tag_month.setData(monthData)
                    purchase_price_tag_year.setData(yearData)

                    purchase_buy_btn.setResource(state.buyBtnText)
                    purchase_year_hint.setResource(state.yearPriceHint)

                    changeLoadingVisibility(needShow = false)
                    changeErrorVisibility(needShow = false)
                }
                PurchaseViewModel.State.Error -> {
                    changeLoadingVisibility(needShow = false)
                    changeErrorVisibility(needShow = true)
                }
                PurchaseViewModel.State.Loading -> {
                    changeErrorVisibility(needShow = false)
                    changeLoadingVisibility(needShow = true)
                }
            }
        }

        viewModel.isMonthSelected.observe(lifecycleOwner, { isSelected ->
            purchase_price_tag_month.isSelected = isSelected
        })

        viewModel.isYearSelected.observe(lifecycleOwner, { isSelected ->
            purchase_price_tag_year.isSelected = isSelected
        })
    }

    private fun changeErrorVisibility(needShow: Boolean) {
        purchase_price_tag_container.isInvisible = needShow
        purchase_buy_btn.isVisible = !needShow
        purchase_year_hint.isVisible = !needShow

        purchase_repeat_txt.isVisible = needShow
        purchase_repeat_btn.isVisible = needShow
    }

    private fun changeLoadingVisibility(needShow: Boolean) {
        purchase_purchase_progress.isVisible = needShow
        purchase_price_tag_container.isInvisible = needShow
        purchase_buy_btn.isVisible = !needShow
        purchase_year_hint.isVisible = !needShow

        purchase_repeat_txt.isVisible = !needShow
        purchase_repeat_btn.isVisible = !needShow
    }
}
