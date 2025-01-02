package com.eazywrite.app.ui.welcome

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.eazywrite.app.R
import com.eazywrite.app.common.toast
import com.eazywrite.app.data.model.LoginBean
import com.eazywrite.app.data.model.SignUpBean
import com.eazywrite.app.data.model.VerifyBean
import com.eazywrite.app.data.network.Network.accountService
import com.eazywrite.app.data.repository.UserRepository
import com.eazywrite.app.databinding.SignupBinding
import com.eazywrite.app.ui.main.MainActivity
import com.eazywrite.app.util.ShowToast
import com.eazywrite.app.util.messageSummary
import com.eazywrite.app.util.setWindow
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

class SignUpActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var countDownTimer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setWindow(false)
        mBinding.post.setOnClickListener(this)
        mBinding.loginBtn.setOnClickListener(this)
        initView()
    }

    private val mBinding: SignupBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.signup)
    }

    private fun initView() {
        val cardView = findViewById<CardView>(R.id.round1)
        cardView.background.alpha = 35
        val cardView1 = findViewById<CardView>(R.id.round2)
        cardView1.background.alpha = 20
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.post -> post()
            R.id.login_btn -> register()
        }
    }

    private fun register() {
        val password = mBinding!!.password.text.toString()
        val verifyCode = mBinding!!.verificationCode.text.toString()
        val postbox = mBinding!!.postbox.text.toString()
        if (postbox == "") {
            toast("邮箱不能为空")
            return
        } else if (verifyCode == "") {
            toast("验证码不能为空")
            return
        } else if (password == "") {
            toast("密码不能为空")
            return
        }
        val bean = LoginBean()
        bean.password = password
        bean.code = verifyCode
        bean.username = postbox
        val json = Gson().toJson(bean)
        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(), json
        )
        val call = accountService.postSignUp(body)
        lifecycleScope.launch {
            kotlin.runCatching {
                val response = withContext(Dispatchers.IO) { call.execute() }
                val b = response.body()
                if (b?.code != 200) throw Exception(b?.msg ?: "")
            }.onSuccess {
                loginUp(postbox, password)
            }.onFailure {
                toast("注册失败：${it.message}")
            }
        }
    }

    suspend fun loginUp(account: String, password: String?) {
        val signUpBean = SignUpBean()
        signUpBean.password = messageSummary(password!!, "SHA-256")
        signUpBean.username = account
        val json = Gson().toJson(signUpBean)
        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(), json
        )
        val call = accountService.postLogin(body)
        val response = withContext(Dispatchers.IO) { call.execute() }
        val responseBody = response.body()
        if (responseBody?.code == 200) {
            kotlin.runCatching {
                UserRepository.login(account)
            }.onSuccess {
                this@SignUpActivity.startActivity(
                    Intent(
                        this@SignUpActivity,
                        MainActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
                this@SignUpActivity.finishAndRemoveTask()
                toast("登录成功")
            }.onFailure {
                it.printStackTrace()
                toast("登录失败：${it.message}")
            }
        } else {
            toast(responseBody?.msg ?: "登录失败")
        }

    }

    var count = 0
    private fun post() {
        val postbox = mBinding!!.postbox.text.toString()
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
        bean.codeType = "1001"
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
                if (response.body()?.code == 200){
                    toast(text = "验证码发送成功")
                } else toast(
                    text = response.body()?.msg ?: ""
                )
            }.onFailure {
                it.printStackTrace()
                toast("验证码获取失败")
            }
        }
    }

    companion object {
        @JvmStatic
        fun jumpLoginActivity(context: Context, activity: LoginActivity?) {
            val intent = Intent(context, SignUpActivity::class.java)
            context.startActivity(intent)
        }
    }
}