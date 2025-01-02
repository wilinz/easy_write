package com.eazywrite.app.ui.bill.fragment

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.eazywrite.app.R
import com.eazywrite.app.common.toast
import com.eazywrite.app.data.model.Bill
import com.eazywrite.app.data.repository.BillRepository
import com.eazywrite.app.databinding.FragmentBillAddItemBinding
import com.eazywrite.app.ui.bill.adapter.ViewPager2Adapter
import com.eazywrite.app.util.ScreenUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

class AddItemFragment : Fragment(), CountInterface, View.OnClickListener {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_bill_add_item, container, false)
        mBinding.count.setOnClickListener(this)
        mBinding.save.setOnClickListener(this)
        mBinding.clear3.setOnClickListener(this)
        mBinding.datePicker.setOnClickListener(this)
        mBinding.back.setOnClickListener(this)
        mBinding.titleName.requestFocus()
        mBinding.statusBarPadding.layoutParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ScreenUtil.getStatusBarHeight(requireContext())
            )
        return mBinding.getRoot()
    }

    private var mDataViewModel: DataViewModel? = null
    private var adapter: ViewPager2Adapter? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mDataViewModel = ViewModelProvider(this).get(
            DataViewModel::class.java
        )
        initDate()
        addNote()
        init()
        back()
        adapter = ViewPager2Adapter(requireActivity(), fragments)
        mBinding!!.viewPager2.adapter = adapter
        TabLayoutMediator(
            mBinding!!.tabLayout,
            mBinding!!.viewPager2
        ) { tab: TabLayout.Tab, position: Int ->
            if (position == 0) {
                tab.text = "支出"
            } else {
                tab.text = "收入"
            }
        }.attach()
        mBinding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // 处理选中Tab的逻辑
                when (tab?.text) {
                    "支出" -> {
                        mDataViewModel!!.inOrOut.value = "out"
                        mBinding.imageId.setImageDrawable(resources.getDrawable(R.drawable.baoxian, null))
                        mBinding.category.text = "保险"
                        mDataViewModel?.category?.value = "保险"

                    }
                    "收入" -> {
                        mDataViewModel!!.inOrOut.value = "in"
                        mBinding.imageId.setImageDrawable(resources.getDrawable(R.drawable.gongzi1, null))
                        mBinding.category.text = "工资"
                        mDataViewModel?.category?.value = "工资"
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // 处理取消选中Tab的逻辑
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // 处理重新选中Tab的逻辑
            }
        })
    }

    private fun initDate() {

        // 获取当前日期
        var currentDate: LocalDate? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            currentDate = LocalDate.now()
            // 将月日转换为指定格式的字符串
            val monthDay = currentDate.format(DateTimeFormatter.ofPattern("M月d日"))

            // 将年份转换为字符串
            val year = currentDate.year.toString()
            mDataViewModel!!.dayMonth.value = monthDay
            mDataViewModel!!.year.value = year
            mBinding!!.datePicker.text = monthDay

            val time = System.currentTimeMillis()
            val dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(
                    time
                ), ZoneId.systemDefault()
            )
            mDataViewModel!!.data.value = dateTime
        }
    }



    var beiZhu = StringBuilder()
    var titleName = StringBuilder()
    private fun addNote() {
        mBinding!!.titleName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                titleName.replace(0, titleName.length, charSequence.toString())
                mDataViewModel!!.titleName.value = titleName.toString()

            }

            override fun afterTextChanged(editable: Editable) {}
        })
        mBinding!!.note.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                beiZhu.replace(0, beiZhu.length, charSequence.toString())
                mDataViewModel!!.note.value = beiZhu.toString()
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }

    private fun back() {
        mBinding!!.back.setOnClickListener { view: View? -> requireActivity().finish() }
    }

    private val fragments = ArrayList<Fragment>()
    private fun init() {
        val fragmentOne = FragmentOne(this)
        val fragmentTwo = FragmentTwo(this)
        fragments.add(fragmentOne)
        fragments.add(fragmentTwo)
    }

    lateinit var mBinding: FragmentBillAddItemBinding
    override fun getCount(count: String) {
        mDataViewModel!!.count.value = count
        mBinding!!.count.text = count
    }

    override fun getImage(imageId: Int) {
        mDataViewModel!!.imageId.value = imageId
        mBinding!!.imageId.setImageResource(imageId)
    }

    override fun getName(name: String) {
        mDataViewModel!!.category.value = name
        mBinding!!.category.text = name
    }

    override fun getInOrOut(inOrOut: String) {
        mDataViewModel!!.inOrOut.value = inOrOut
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.count -> {
                val dialogFragment: BottomSheetDialogFragment =
                    MyDialogFragment(this@AddItemFragment)
                dialogFragment.show(activity?.supportFragmentManager!!, "DialogFragment")

            }
            R.id.clear3 -> clear()
            R.id.datePicker -> dataPicker()
            R.id.save -> save()
            R.id.back -> {
                clear()
                requireActivity().finish()
            }
        }
    }

    private fun save() {
        val outputBean = OutputBean()
        outputBean.date = StringBuilder(mDataViewModel!!.dayMonth.value)
        outputBean.beiZhu = StringBuilder(mDataViewModel!!.note.value)
        outputBean.moneyCount = StringBuilder(mDataViewModel!!.count.value)
        outputBean.category = mDataViewModel!!.category.value
        outputBean.imageId = mDataViewModel!!.imageId.value!!
        outputBean.category = mDataViewModel!!.category.value
        outputBean.dayMonth = mDataViewModel!!.dayMonth.value
        outputBean.year = mDataViewModel!!.year.value
        outputBean.inOrOut = mDataViewModel!!.inOrOut.value
        outputBean.name = mDataViewModel!!.titleName.value
        Log.d("HelloWorld", "save: ${mDataViewModel!!.titleName.value}")
        outputBean.time = mDataViewModel!!.data.value
        val count = outputBean.moneyCount.toString().toBigDecimal()

        if (outputBean.category == null) {
            toast("消费类型不能为空")
        } else {
            val bill = Bill(
                amount = count,
                comment = outputBean.beiZhu.toString(),
                datetime = outputBean.time,
                category = outputBean.category,
                name = outputBean.name,
                type = outputBean.inOrOut
            )

            lifecycleScope.launch() {
                kotlin.runCatching {
                    BillRepository.addBill(bill = bill)
                }.onSuccess {
                    toast(text = "成功")
                    requireActivity().finish()
                }.onFailure { e ->
                    toast(text = "失败：${e.message}")
                }
            }

        }

    }


    fun clear() {
        mDataViewModel!!.clear()
        val emptyBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        mBinding!!.imageId.setImageBitmap(emptyBitmap)
        mBinding!!.titleName.setText("账目名称")
        mBinding!!.category.text = "账目类别"
        mBinding!!.note.setText("")
        mBinding!!.count.text = "0.00"
        mBinding!!.titleName.clearFocus()
        mBinding!!.note.clearFocus()
        initDate()
        fragments.clear()
        init()
        adapter = ViewPager2Adapter(requireActivity(), fragments)
        mBinding!!.viewPager2.adapter = adapter
    }

    private fun dataPicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTheme(R.style.ThemeOverlay_App_DatePicker)
            .setTitleText("日期选择").build()
        datePicker.addOnPositiveButtonClickListener { selection: Long? ->
            val dateStr = DateFormat.getDateInstance().format(
                Date(
                    selection!!
                )
            )
            val yearStr = dateStr.substring(0, 4) + "年"
            val monthDayStr = dateStr.substring(5)
            val dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(
                    selection
                ), ZoneId.systemDefault()
            )
            mDataViewModel!!.data.value = dateTime
            mBinding!!.datePicker.text = monthDayStr
            mDataViewModel!!.dayMonth.value = monthDayStr
            mDataViewModel!!.year.setValue(yearStr)
        }
        datePicker.addOnNegativeButtonClickListener { view: View? -> }
        datePicker.show(requireActivity().supportFragmentManager, "data")
    }
}