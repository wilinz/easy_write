package com.eazywrite.app.ui.welcome

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.eazywrite.app.R
import com.eazywrite.app.common.toast
import com.eazywrite.app.data.model.SignUpBean
import com.eazywrite.app.data.network.Network.accountService
import com.eazywrite.app.data.repository.UserRepository
import com.eazywrite.app.databinding.FragmentLoginBinding
import com.eazywrite.app.ui.main.MainActivity
import com.eazywrite.app.ui.welcome.SignUpActivity.Companion.jumpLoginActivity
import com.eazywrite.app.util.messageSummary
import com.eazywrite.app.util.setWindow
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setWindow(false)
        this.setContentView(R.layout.fragment_login)
        mActivity = this
        initView()
    }

    private val binding: FragmentLoginBinding by lazy {
        DataBindingUtil.setContentView(
            this,
            R.layout.fragment_login
        )
    }

    private fun initView() {
        val cardView = findViewById<CardView>(R.id.round1)
        cardView.background.alpha = 35
        val cardView1 = findViewById<CardView>(R.id.round2)
        cardView1.background.alpha = 20
        binding.forgetPassword.setOnClickListener(this)
        binding.signup.setOnClickListener(this)
        binding.login.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.forget_password -> ForgetPasswordActivity.jumpForgetActivity(this)
            R.id.signup -> jumpLoginActivity(this, this)
            R.id.login -> loginUp()
        }
    }

    var mActivity: LoginActivity? = null
    private fun loginUp() {
        val account = binding!!.account.text.toString()
        val password = binding!!.passwordWelcome.text.toString()
        if (account == "") {
            toast("用户名不能为空")
            return
        } else if (password == "") {
            toast("密码不能为空")
            return
        }
        val signUpBean = SignUpBean()
        signUpBean.password = messageSummary(password, "SHA-256")
        signUpBean.username = account
        val json = Gson().toJson(signUpBean)
        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(), json
        )
        val call = accountService.postLogin(body)
        lifecycleScope.launch {
            kotlin.runCatching {
                val response = withContext(Dispatchers.IO) { call.execute() }
                val body = response.body()
                if (body?.code == 200) {
                    kotlin.runCatching {
                        UserRepository.login(account)
                    }.onSuccess {
                        mActivity!!.finish()
                        MainActivity.jumpMainActivity(this@LoginActivity)
                        toast(text = "登录成功")
                    }.onFailure {
                        it.printStackTrace()
                        toast(text = "登录失败：${it.message}")
                    }
                } else {
                    toast(body?.msg ?: "登录失败")
                }
            }.onFailure {
                toast("注册失败：${it.message}")
            }
        }
    }
}