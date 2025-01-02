package com.eazywrite.app.common


import android.content.Context
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.annotation.StringRes
import com.eazywrite.app.MyApplication
import java.lang.ref.WeakReference

private var toast: WeakReference<Toast>? = null

fun toast(
    @StringRes resId: Int,
    context: Context = MyApplication.instance,
    duration: Int = LENGTH_SHORT
) {
    toast(context.getString(resId), context, duration)
}

@JvmOverloads
fun toast(text: String, context: Context = MyApplication.instance, duration: Int = LENGTH_SHORT) {
    toast?.get()?.cancel()//取消之前的toast
    toast = WeakReference(Toast.makeText(context.applicationContext, text, duration))//创建新toast
    toast?.get()?.show()
}