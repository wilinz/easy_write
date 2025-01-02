package com.eazywrite.app.ui.profile.feedback

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.CallSuper

data class GetContentWithTagArgs<T>(
    val category: String,
    val tag: T
)

data class GetContentWithTagResult<T>(
    val uri: Uri?,
    val tag: T?
)

open class GetContentWithTag<T> :
    ActivityResultContract<GetContentWithTagArgs<T>, GetContentWithTagResult<T>>() {

    private var tag: T? = null

    @CallSuper
    override fun createIntent(context: Context, input: GetContentWithTagArgs<T>): Intent {
        tag = input.tag
        return Intent(Intent.ACTION_GET_CONTENT)
            .addCategory(Intent.CATEGORY_OPENABLE)
            .setType(input.category)
    }

    final override fun getSynchronousResult(
        context: Context,
        input: GetContentWithTagArgs<T>
    ): SynchronousResult<GetContentWithTagResult<T>>? = null

    final override fun parseResult(resultCode: Int, intent: Intent?): GetContentWithTagResult<T> {
        val uri = intent.takeIf { resultCode == Activity.RESULT_OK }?.data
        return GetContentWithTagResult(uri, tag)
    }
}