package com.rvv.android.sample

import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.card.MaterialCardView

data class PriceTagViewData(
    val priceText: String,
    val crossedPriceText: String?,
    val payPeriodText: String,
    val planName: String,
    val perPeriodText: String,
    val additionalText: String?,
)

class PriceTagView : FrameLayout {

    private lateinit var title: TextView
    private lateinit var price: TextView
    private lateinit var crossedPrice: TextView
    private lateinit var period: TextView
    private lateinit var perPeriod: TextView
    private lateinit var additionalText: TextView
    private lateinit var root: MaterialCardView

    private var isSelected = false

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(ctx: Context, attrs: AttributeSet?, defStyle: Int) : super(ctx, attrs, defStyle) {
        init()
    }

    fun setData(data: PriceTagViewData) {
        val (priceText, crossedPriceText, periodText, titleText, perPeriodText, additional) = data

        price.text = priceText
        crossedPrice.isVisible = crossedPriceText != null
        crossedPriceText?.let { crossedPrice.text = crossedPriceText }
        period.text = periodText
        title.text = titleText
        perPeriod.text = perPeriodText

        additionalText.isVisible = additional != null
        additional?.let { additionalText.text = additional }
    }

    override fun setSelected(selected: Boolean) {
        isSelected = selected

        val gray = ContextCompat.getColor(context, R.color.gray)
        val textColor = ContextCompat.getColor(context, R.color.textColor)
        val color = if (selected) context.accentColor else gray
        val priceTextColor = if (selected) textColor else gray

        root.strokeColor = color
        title.setBackgroundColor(color)
        period.setTextColor(color)

        perPeriod.setTextColor(priceTextColor)
        price.setTextColor(priceTextColor)
        crossedPrice.setTextColor(priceTextColor)
    }

    override fun isSelected(): Boolean = isSelected

    private fun init() {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.view_price_tag, this, true)

        title = findViewById(R.id.price_tag_title)
        period = findViewById(R.id.price_tag_period)
        price = findViewById(R.id.price_tag_price)
        crossedPrice = findViewById(R.id.price_tag_crossed_price)
        perPeriod = findViewById(R.id.price_tag_per_period)
        root = findViewById(R.id.price_tag_card_root)
        additionalText = findViewById(R.id.price_tag_additional_text)

        crossedPrice.paintFlags = crossedPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        setSelected(false)

        if (isInEditMode) {
            val planName = context.getString(R.string.purchase_plan_pay_period_month)
            val perPeriodText = context.getString(R.string.purchase_per_month)
            val planPayPeriod = context.getString(R.string.purchase_plan_pay_period_month)
            setData(
                PriceTagViewData(
                    "33 ₽", "99 ₽", planPayPeriod, planName, perPeriodText, "первые 12 месяцев"
                )
            )
        }
    }
}
