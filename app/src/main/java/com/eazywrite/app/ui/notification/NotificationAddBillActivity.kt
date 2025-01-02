package com.eazywrite.app.ui.notification

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.eazywrite.app.R
import com.eazywrite.app.common.toast
import com.eazywrite.app.data.model.Bill
import com.eazywrite.app.data.repository.BillRepository
import com.eazywrite.app.ui.bill.fragment.OutputBean
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

open class NotificationAddBillActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_add_bill)
        val window = window
        window.setGravity(Gravity.TOP)
        window.decorView.systemUiVisibility = systemUiVisibility
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
        val layoutParams = window.attributes //获取dialog布局的参数
        if (backgroundDrawable != null) {
            window.setBackgroundDrawable(backgroundDrawable)
        }
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT //全屏
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT

        //设置导航栏颜
        window.navigationBarColor = Color.TRANSPARENT
        //内容扩展到导航栏
        window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL)
        if (Build.VERSION.SDK_INT >= 28) {
            layoutParams.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        window.setBackgroundDrawableResource(R.drawable.bg_add_dialog)
        window.attributes = layoutParams
        initView()
    }

    private fun initView() {
        val tabLayout = findViewById<TabLayout>(R.id.notify_tablelayout)
        tabLayout.getTabAt(0)!!.select()
        tabLayout.getTabAt(0)!!.view.setOnClickListener { view: View? ->
            tabLayout.getTabAt(0)!!
                .select()
        }
        tabLayout.getTabAt(1)!!.view.setOnClickListener { view: View? ->
            tabLayout.getTabAt(1)!!
                .select()
        }
        findViewById<View>(R.id.ic_back).setOnClickListener { view: View? -> finish() }
        findViewById<View>(R.id.textView8).setOnClickListener { view: View? -> finish() }
        val editText = findViewById<EditText>(R.id.inputBill)
        val editText2 = findViewById<EditText>(R.id.textView10)
        findViewById<View>(R.id.saveBill).setOnClickListener(View.OnClickListener {
            val outputBean = OutputBean()
            outputBean.moneyCount = StringBuilder(editText.text.toString())
            val localDateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(System.currentTimeMillis()),
                ZoneId.systemDefault()
            )
            outputBean.time = localDateTime
            val monthDay = LocalDate.now().format(DateTimeFormatter.ofPattern("M月d日"))
            outputBean.date = StringBuilder(monthDay)
            outputBean.dayMonth = monthDay
            val year = LocalDate.now().year.toString()
            outputBean.year = year
            outputBean.inOrOut = tabLayout.getTabAt(1)!!.isSelected.toString()
            outputBean.imageId = 0
            outputBean.name = editText2.text.toString()
            val count = outputBean.moneyCount.toString()
            outputBean.category = "未设置"
            val amount = count.toBigDecimalOrNull() ?: return@OnClickListener kotlin.run {
                toast("金额输入错误")
            }
            if (outputBean.time == null || outputBean.category == null) return@OnClickListener
            val bill = Bill(
                localId = 0,
                cloudId = 0,
                username = "",
                amount = amount,
                comment = "",
                datetime = outputBean.time,
                date = outputBean.time.toLocalDate(),
                category = outputBean.category,
                transactionPartner = "",
                name = outputBean.name,
                type = if (outputBean.inOrOut == "true") Bill.TYPE_IN else Bill.TYPE_OUT,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                deletedAt = null,
                isSynced = false
            )
            lifecycleScope.launch {
                kotlin.runCatching {
                    BillRepository.addBill(bill)
                }.onSuccess {
                    toast("添加成功")
                    finish()
                }.onFailure {
                    it.printStackTrace()
                    toast("添加失败：${it.message}")
                }
            }

        })
    }

    //设置背景颜色
    private val backgroundDrawable: Drawable?
        get() = null
    private val systemUiVisibility: Int
        get() = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
}