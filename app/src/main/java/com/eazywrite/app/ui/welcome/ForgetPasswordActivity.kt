package com.eazywrite.app.ui.welcome

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.eazywrite.app.R
import com.eazywrite.app.common.toast
import com.eazywrite.app.data.model.ResetBean
import com.eazywrite.app.data.model.VerifyBean
import com.eazywrite.app.data.network.Network.accountService
import com.eazywrite.app.databinding.ActivityFogetPasswordBinding
import com.eazywrite.app.util.setWindow
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

class ForgetPasswordActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setWindow(false)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_foget_password)
        setOnClickListener()
    }

    private fun setOnClickListener() {
        mBinding!!.loginAndLogUp.setOnClickListener(this)
        mBinding!!.gain.setOnClickListener(this)
    }

    var mBinding: ActivityFogetPasswordBinding? = null
    override fun onClick(view: View) {
        when (view.id) {
            R.id.loginAndLogUp -> loginAndLogUp()
            R.id.gain -> post()
        }
    }

    private fun loginAndLogUp() {
        val postbox = mBinding!!.postboxTwo.text.toString()
        if (postbox == "") {
            toast("输入邮箱不能为空")
            return
        }
        val code = mBinding!!.code.text.toString()
        if (code == "") {
            toast("验证码不能为空")
            return
        }
        val passwordTwo = mBinding!!.passwordTwo.text.toString()
        if (passwordTwo == "") {
            toast("新密码不能为空")
            return
        }
        val passwordThree = mBinding!!.passwordThree.text.toString()
        if (passwordThree == "") {
            toast("再次输入新密码不能为空")
            return
        }
        if (passwordThree != passwordTwo) {
            toast("两次输入的密码不相等")
            return
        }
        val resetBean = ResetBean()
        resetBean.code = code
        resetBean.username = postbox
        resetBean.newPassword = passwordThree
        resetBean.getAll()
        val json = Gson().toJson(resetBean)
        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(), json
        )
        val call = accountService.postReset(body)
        lifecycleScope.launch {
            kotlin.runCatching {
                val response = withContext(Dispatchers.IO) { call.execute() }
                val body = response.body()
                if (body?.code == 200) {
                    toast("重置密码成功")
                    startActivity(Intent(this@ForgetPasswordActivity, LoginActivity::class.java))
                    mForgetPasswordActivity.finish()
                } else {
                    toast(body?.msg ?: "重置密码失败")
                }
            }.onFailure {
                toast("注册失败：${it.message}")
            }
        }
    }

    private lateinit var countDownTimer: CountDownTimer
    var mForgetPasswordActivity = this
    var count = 0
    private fun post() {
        val postbox = mBinding!!.postboxTwo.text.toString()
        if (postbox == "") {
            toast("输入邮箱不能为空")
            return
        }
        if(count==0){
            count++
            countDownTimer = object : CountDownTimer(8000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    mBinding?.gainYanZhenMa?.text = "倒计时: ${millisUntilFinished / 1000}"
                }

                override fun onFinish() {
                    mBinding?.gainYanZhenMa?.text = "发送新的验证码"
                    count = 0
                }
            }.start()
        }else{
            toast("请等待")
            return
        }


        val bean = VerifyBean()
        bean.codeType = "1002"
        bean.graphicCode = "忽略"
        bean.phoneOrEmail = postbox
        val json = Gson().toJson(bean)
        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(), json
        )
        val call = accountService.postVerify(body)
        lifecycleScope.launch{
            kotlin.runCatching {
                val response = withContext(Dispatchers.IO) {call.execute()}
                if (response.body()?.code == 200) toast(text = "验证码发送成功") else toast(
                    text = response.body()?.msg ?: ""
                )
            }.onFailure {
                it.printStackTrace()
                toast("获取验证码失败")
            }
        }
    }

    companion object {
        fun jumpForgetActivity(context: Context) {
            val intent = Intent(context, ForgetPasswordActivity::class.java)
            context.startActivity(intent)
        }
    }
}