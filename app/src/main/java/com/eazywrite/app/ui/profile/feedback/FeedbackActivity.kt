package com.eazywrite.app.ui.profile.feedback

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.eazywrite.app.R
import com.eazywrite.app.data.model.RegisterResponse
import com.eazywrite.app.data.network.Network.accountService
import com.eazywrite.app.databinding.ActivityFeedbackBinding
import com.eazywrite.app.util.ShowToast
import com.eazywrite.app.util.copyToCacheFile
import com.eazywrite.app.util.setWindow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File

class FeedbackActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mModel = ViewModelProvider(this).get(SendViewModel::class.java)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_feedback)
        setOnClickListener()
        setWindow(isDarkStatusBarIcon = true, isDecorFitsSystemWindows = true)
    }

    lateinit var mModel: SendViewModel
    private fun setOnClickListener() {
        mBinding!!.send.setOnClickListener(this)
        mBinding!!.advice.setOnClickListener(this)
        mBinding!!.bug.setOnClickListener(this)
        mBinding!!.feedback.setOnClickListener(this)
        mBinding!!.help.setOnClickListener(this)
        mBinding!!.number.setOnClickListener(this)
        mBinding!!.choose1.setOnClickListener(this)
        mBinding!!.back3.setOnClickListener(this)
        mBinding!!.choose2.setOnClickListener(this)
        mBinding!!.choose3.setOnClickListener(this)
        setBackground(1)
        setLabel("建议")
    }

    var mBinding: ActivityFeedbackBinding? = null
    override fun onClick(view: View) {
        when (view.id) {
            R.id.back3 -> {
                finish()
            }
            R.id.choose1 -> {
                val args = GetContentWithTagArgs("image/*", 1)
                launcher.launch(args)
            }
            R.id.choose2 -> {
                val args = GetContentWithTagArgs("image/*", 2)
                launcher.launch(args)
            }
            R.id.choose3 -> {
                val args = GetContentWithTagArgs("image/*", 3)
                launcher.launch(args)
            }
            R.id.send -> {
                //编写发送信息的逻辑
                sendFeedback()
            }
            R.id.feedback -> {
                setFeedback()
            }
            R.id.advice -> {
                setBackground(1)
                setLabel("建议")
            }
            R.id.bug -> {
                setBackground(2)
                setLabel("bug")
            }
            R.id.help -> {
                setBackground(3)
                setLabel("帮助")
            }
            R.id.number -> {
                number
            }
        }
    }


    var mFiles = ArrayList<File>()
    private fun sendFeedback() {

        lifecycleScope.launch {
            var multipartBody: MultipartBody

            if (mFiles.size.equals(0)) {

                multipartBody =
                    MultipartBody.Builder("WebAppBoundary")
                        .addFormDataPart("label", mModel!!.getLabel().value.toString())
                        .addFormDataPart("phone", mModel!!.getNumber().value.toString())
                        .addFormDataPart("feedback", mModel!!.getFeedback().value.toString())
                        .build()
                callRequest(multipartBody)
            }
            if (mFiles.size.equals(1)) {
                multipartBody =
                    MultipartBody.Builder("WebAppBoundary")
                        .addFormDataPart("picture", "picture1.jpg", mFiles[0]!!.asRequestBody())
                        .addFormDataPart("label", mModel!!.getLabel().value.toString())
                        .addFormDataPart("phone", mModel!!.getNumber().value.toString())
                        .addFormDataPart("feedback", mModel!!.getFeedback().value.toString())
                        .build()
                callRequest(multipartBody)
            }
            if (mFiles.size.equals(2)) {
                multipartBody =
                    MultipartBody.Builder("WebAppBoundary")
                        .addFormDataPart("picture", "picture1.jpg", mFiles[0]!!.asRequestBody())
                        .addFormDataPart("picture", "picture2.jpg", mFiles[1]!!.asRequestBody())
                        .addFormDataPart("label", mModel!!.getLabel().value.toString())
                        .addFormDataPart("phone", mModel!!.number.value.toString())
                        .addFormDataPart("feedback", mModel!!.getFeedback().value.toString())
                        .build()
                callRequest(multipartBody)
            }
            if (mFiles.size.equals(3)) {
                multipartBody =
                    MultipartBody.Builder("WebAppBoundary")
                        .addFormDataPart("picture", "picture1.jpg", mFiles[0]!!.asRequestBody())
                        .addFormDataPart("picture", "picture2.jpg", mFiles[1]!!.asRequestBody())
                        .addFormDataPart("picture", "picture3.jpg", mFiles[2]!!.asRequestBody())
                        .addFormDataPart("label", mModel!!.getLabel().value.toString())
                        .addFormDataPart("phone", mModel!!.number.value.toString())
                        .addFormDataPart("feedback", mModel!!.getFeedback().value.toString())
                        .build()
                callRequest(multipartBody)
            }


        }

    }

    private fun callRequest(multipartBody: MultipartBody) {
        val call = accountService.feedback(multipartBody)
        call.enqueue(object : retrofit2.Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                ShowToast.showToast(this@FeedbackActivity,"提交成功")
                finish()
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                t.printStackTrace()
                ShowToast.showToast(this@FeedbackActivity,"提交失败")
            }
        })
    }

    private fun setFeedback() {
        mBinding!!.feedback.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                mModel!!.getFeedback().value = charSequence.toString()
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }

    private fun setBackground(i: Int) {
        if (i == 1) {
            mBinding!!.advice.setBackgroundColor(Color.parseColor("#FFEFD4"))
            mBinding!!.bug.setBackgroundColor(Color.parseColor("#FFFFFF"))
            mBinding!!.help.setBackgroundColor(Color.parseColor("#FFFFFF"))
        } else if (i == 2) {
            mBinding!!.advice.setBackgroundColor(Color.parseColor("#FFFFFF"))
            mBinding!!.bug.setBackgroundColor(Color.parseColor("#FFEFD4"))
            mBinding!!.help.setBackgroundColor(Color.parseColor("#FFFFFF"))
        } else if (i == 3) {
            mBinding!!.advice.setBackgroundColor(Color.parseColor("#FFFFFF"))
            mBinding!!.bug.setBackgroundColor(Color.parseColor("#FFFFFF"))
            mBinding!!.help.setBackgroundColor(Color.parseColor("#FFEFD4"))
        }
    }

    private fun setLabel(label: String) {
        mModel!!.getLabel().value = label
    }

    val number: Unit
        get() {
            mBinding!!.number.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    charSequence: CharSequence,
                    i: Int,
                    i1: Int,
                    i2: Int
                ) {
                }

                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                    mModel!!.getNumber().value = charSequence.toString()
                }

                override fun afterTextChanged(editable: Editable) {}
            })
        }
    private var launcher =
        registerForActivityResult(GetContentWithTag()) { (uri, tag): GetContentWithTagResult<Int> ->

            if (uri == null) return@registerForActivityResult

            lifecycleScope.launch {

                when (tag) {
                    1 -> {
                        if (mFiles.size >= 1) mFiles.subList(0, 0)
                        mFiles.add(uri.copyToCacheFile(this@FeedbackActivity))
                    }
                    2 -> {
                        if (mFiles.size >= 2) mFiles.subList(1, 1)
                        mFiles.add(uri.copyToCacheFile(this@FeedbackActivity))
                    }
                    3 -> {
                        if (mFiles.size >= 3) mFiles.subList(2, 2)
                        mFiles.add(uri.copyToCacheFile(this@FeedbackActivity))
                    }
                }
            }
            when (tag) {
                1 -> {
                    Glide.with(this).load(uri).into(mBinding!!.choose1)
                    mBinding!!.choose2.visibility = View.VISIBLE
                }
                2 -> {

                    Glide.with(this).load(uri).into(mBinding!!.choose2)
                    mBinding!!.choose3.visibility = View.VISIBLE
                }
                3 -> {
                    Glide.with(this).load(uri).into(mBinding!!.choose3)
                }
            }
        }
}