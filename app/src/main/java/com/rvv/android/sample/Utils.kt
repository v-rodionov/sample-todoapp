package com.rvv.android.sample

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

val Context.app
    get() = applicationContext as App

inline fun <reified VM : ViewModel> Fragment.getViewModel(provider: ViewModelProvider.Factory) =
    ViewModelProvider(this, provider).get(VM::class.java)

inline fun <reified P> Fragment.getComponentProvider(): P = activity as P

val Context.accentColor: Int
    get() = getColorCompat(R.color.colorAccent)

fun Context.getColorCompat(@ColorRes colorResId: Int) = ContextCompat.getColor(this, colorResId)

fun TextView.setLinks(
    list: List<Pair<String, () -> Unit>>,
    @StringRes template: Int,
    @ColorRes colorResId: Int,
) {
    val textList = list.map { textAndAction -> textAndAction.first }
    val fullText = context.getString(template, *textList.toTypedArray())
    val spannable = SpannableString(fullText)
    val flags = Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    val color = context.getColorCompat(colorResId)

    list.forEach { textAndAction ->
        val (text, action) = textAndAction
        val startSpanIndex = fullText.indexOf(text)
        val endSpanIndex = startSpanIndex + text.length

        val clickSpan = object : ClickableSpan() {
            override fun onClick(widget: View) = action()

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
            }
        }

        val colorSpan = ForegroundColorSpan(color)

        spannable.setSpan(colorSpan, startSpanIndex, endSpanIndex, flags)
        spannable.setSpan(clickSpan, startSpanIndex, endSpanIndex, flags)
    }

    movementMethod = LinkMovementMethod.getInstance()
    setText(spannable, TextView.BufferType.SPANNABLE)
}