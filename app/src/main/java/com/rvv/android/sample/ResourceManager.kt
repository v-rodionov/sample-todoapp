@file:JvmName("ResourceManager")

package com.rvv.android.sample

import android.content.Context
import android.widget.TextView
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes

sealed class StringResource

data class IdStringResource(@StringRes val id: Int, val args: List<Any?>? = null) : StringResource()

data class TextStringResource(val text: String) : StringResource()

data class PluralStringResource(
    @PluralsRes val id: Int,
    val quantity: Int,
    val args: List<Any>,
) : StringResource()

fun stringResourceOf(text: String): StringResource = TextStringResource(text)

fun stringResourceOf(@StringRes id: Int, vararg args: Any?): StringResource {
    return IdStringResource(id, args.asList())
}

fun emptyStringResource(): StringResource = stringResourceOf("")

fun pluralStringResourceOf(@PluralsRes id: Int, quantity: Int, vararg args: Any): StringResource {
    return PluralStringResource(id, quantity, args.toList())
}

fun StringResource?.text(context: Context): String {
    return when (this) {
        is IdStringResource -> {
            if (args != null) context.getString(id, *args.toTypedArray()) else context.getString(id)
        }
        is TextStringResource -> text
        is PluralStringResource -> {
            context.resources.getQuantityString(id, quantity, *args.toTypedArray())
        }
        null -> ""
    }
}

fun TextView.setResource(res: StringResource?) {
    text = res.text(context)
}
