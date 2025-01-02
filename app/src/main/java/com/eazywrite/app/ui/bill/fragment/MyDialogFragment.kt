package com.eazywrite.app.ui.bill.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.eazywrite.app.R
import com.eazywrite.app.common.toast
import com.eazywrite.app.databinding.DialogFragmentBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import net.sourceforge.jeval.EvaluationException
import net.sourceforge.jeval.Evaluator
import java.math.BigDecimal
import java.util.Locale
import java.util.Stack

class MyDialogFragment(private val mAddItemFragment: AddItemFragment) : BottomSheetDialogFragment(),
    View.OnClickListener {
    var mBinding: DialogFragmentBinding? = null

    var mBuilder: StringBuilder = StringBuilder("")


    var isLeftDot: Boolean = false
    var isCul: Boolean = false


    var isRightDot: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        dialog!!.window!!.setDimAmount(0f)

        mBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_fragment, container, false)

        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding!!.money.text = mBuilder
        setClickListener()
    }

    private fun setClickListener() {
        mBinding!!.zero.setOnClickListener(this)
        mBinding!!.one.setOnClickListener(this)
        mBinding!!.two.setOnClickListener(this)
        mBinding!!.three.setOnClickListener(this)
        mBinding!!.four.setOnClickListener(this)
        mBinding!!.five.setOnClickListener(this)
        mBinding!!.six.setOnClickListener(this)
        mBinding!!.seven.setOnClickListener(this)
        mBinding!!.eight.setOnClickListener(this)
        mBinding!!.nine.setOnClickListener(this)
        mBinding!!.finish.setOnClickListener(this)
        mBinding!!.dot.setOnClickListener(this)
        mBinding!!.delete.setOnClickListener(this)
        mBinding!!.mul.setOnClickListener(this)
        mBinding!!.div.setOnClickListener(this)
        mBinding!!.clear2.setOnClickListener(this)
        mBinding!!.add.setOnClickListener(this)
        mBinding!!.sub.setOnClickListener(this)
    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.delete -> delete()
            R.id.dot -> add1('.')

            R.id.zero -> //                if(mBuilder.length()<=8) add('0');
                add1('0')

            R.id.one -> //                if(mBuilder.length()<=8) add('1');
                add1('1')

            R.id.two -> //                if(mBuilder.length()<=8) add('2');
                add1('2')

            R.id.three -> //                if(mBuilder.length()<=8) add('3');
                add1('3')

            R.id.four -> //                if(mBuilder.length()<=8) add('4');
                add1('4')

            R.id.five -> //                if(mBuilder.length()<=8) add('5');
                add1('5')

            R.id.six -> //                if(mBuilder.length()<=8) add('6');
                add1('6')

            R.id.seven -> //                if(mBuilder.length()<=8) add('7');
                add1('7')

            R.id.eight -> //                if(mBuilder.length()<=8)add('8');
                add1('8')

            R.id.nine -> //                if(mBuilder.length()<=8)add('9');
                add1('9')

            R.id.finish -> {
                if (mBuilder.length == 0) return
                try {
                    var result = Evaluator().evaluate(mBuilder.toString())
                    result = String.format(Locale.getDefault(), "%.2f", BigDecimal(result))
                    mBuilder.delete(0, mBuilder.length)
                    mBuilder.append(result)
                    mBinding!!.money.text = result
                    mAddItemFragment.getCount(result)
                } catch (e: EvaluationException) {
                    toast("输入有误")
                    e.printStackTrace()
                } catch (e: Exception) {
                    toast("计算出错：" + e.message)
                }
            }

            R.id.add -> //                if(mBuilder.length()<=8&&!isCul){
//
//                    isCul = true;
//                    add('+');
//                }
                add1('+')

            R.id.sub -> //                if(mBuilder.length()<=8&&!isCul){
//                    isCul = true;
//                    add('-');
//                }
                add1('-')

            R.id.mul -> //                if(mBuilder.length()<=8&&!isCul){
//                    isCul = true;
//                    add('*');
//                }
                add1('*')

            R.id.div -> //                if(mBuilder.length()<=8&&!isCul){
//                    isCul = true;
//                    add('/');
//                }
                add1('/')

            R.id.clear2 -> {
                mBuilder.delete(0, mBuilder.length)
                //                mBinding.money.setText("0");
                mBinding!!.money.text = ""
                isCul = false
                isLeftDot = false
                isRightDot = false
            }
        }
    }


    private fun delete() {
        if (mBuilder.length == 0) {
        } else {
            if (mBuilder.substring(mBuilder.length - 1) == "/" || mBuilder.substring(mBuilder.length - 1) == "*" || mBuilder.substring(
                    mBuilder.length - 1
                ) == "+" || mBuilder.substring(mBuilder.length - 1) == "-"
            ) {
                isCul = false
                isRightDot = false

                /*
                 * 有是1
                 * 没有是1
                 * */
                //判断删除符号时，左侧有没有小数点
                isLeftDot = if (mBuilder.indexOf(".") == -1) {
                    false
                } else {
                    true
                }
            }
            if (mBuilder.substring(mBuilder.length - 1) == ".") {
                if (isCul) {
                    isRightDot = true
                } else {
                    isLeftDot = false
                }
            }
            if (mBuilder.length == 1) {
                isLeftDot = false
                isCul = false
                isRightDot = false
            }


            mBuilder.setLength(mBuilder.length - 1)
            mBinding!!.money.text = mBuilder
        }
    }


    private fun add1(s: Char) {
        mBuilder.append(s)
        mBinding!!.money.text = mBuilder
    }

    private fun add(s: Char) {
        var isOk = true

        if (s == '0') {
            if (mBuilder.toString() != "") {
                //判断前一位是不是0，并且是不是小数点前的0
                if (mBuilder.substring(mBuilder.length - 1) == "0" && mBuilder.length == 1) {
                    isOk = false
                }
                if (isCul) {
                    if (mBuilder.substring(mBuilder.length - 1) == "0") {
                        val temp = mBuilder.toString()[mBuilder.length - 2]
                        if (temp == '-' || temp == '/' || temp == '*' || temp == '+') {
                            isOk = false
                        }
                    }
                }
            }
        }

        if (s == '-' || s == '+' || s == '*' || s == '/') {
            isCul = true
            isRightDot = true
            isLeftDot = true
            if (mBuilder.toString() == "") {
                isOk = false
                isCul = false
                isRightDot = false
                isLeftDot = false
            } else {
                if (mBuilder.substring(mBuilder.length - 1) == ".") {
                    mBuilder.setLength(mBuilder.length - 1)
                }
            }
        }

        if (s == '.') {
            if (mBuilder.toString() == "") {
                isOk = false
                isLeftDot = false
            } else {
                if (mBuilder.substring(mBuilder.length - 1) == "-" || mBuilder.substring(mBuilder.length - 1) == "+" || mBuilder.substring(
                        mBuilder.length - 1
                    ) == "*" || mBuilder.substring(mBuilder.length - 1) == "/"
                ) {
                    isOk = false
                    isRightDot = true
                }
            }
        }

        if (s == '1' || s == '2' || s == '3' || s == '4' || s == '5' || s == '6' || s == '7' || s == '8' || s == '9') {
            if (mBuilder.toString() != "") {
                //第一位是0，后面是数字，就把他删掉
                if (mBuilder.substring(0) == "0" && mBuilder.length == 1) {
                    mBuilder.setLength(mBuilder.length - 1)
                }
            }
            //右侧数字
            if (isCul) {
                if (mBuilder.indexOf("+") != -1) {
                    if (mBuilder.substring(mBuilder.indexOf("+") + 1) == "0" && mBuilder.length == mBuilder.indexOf(
                            "+"
                        ) + 2
                    ) {
                        mBuilder.setLength(mBuilder.length - 1)
                    }
                } else if (mBuilder.indexOf("-") != -1) {
                    if (mBuilder.substring(mBuilder.indexOf("-") + 1) == "0" && mBuilder.length == mBuilder.indexOf(
                            "-"
                        ) + 2
                    ) {
                        mBuilder.setLength(mBuilder.length - 1)
                    }
                } else if (mBuilder.indexOf("*") != -1) {
                    if (mBuilder.substring(mBuilder.indexOf("*") + 1) == "0" && mBuilder.length == mBuilder.indexOf(
                            "*"
                        ) + 2
                    ) {
                        mBuilder.setLength(mBuilder.length - 1)
                    }
                } else if (mBuilder.indexOf("/") != -1) {
                    if (mBuilder.substring(mBuilder.indexOf("/") + 1) == "0" && mBuilder.length == mBuilder.indexOf(
                            "/"
                        ) + 2
                    ) {
                        mBuilder.setLength(mBuilder.length - 1)
                    }
                }
            }
        }
        if (isOk) {
            mBuilder.append(s)
            mBinding!!.money.text = mBuilder
        }
    }

    companion object {
        fun toInfixExpression(string: String): List<String> {
            var index = 0
            val ls: MutableList<String> = ArrayList()
            while (index < string.length) {
                if (string[index] == '+' || string[index] == '-' || string[index] == '*' || string[index] == '/') {
                    ls.add(string[index].toString() + "")
                    index++
                } else {
                    var str = ""
                    while (index < string.length && ((string[index] >= '0' && string[index] <= '9') || string[index] == '.')) {
                        str += string[index].toString() + ""
                        index++
                    }
                    ls.add(str)
                }
            }
            return ls
        }

        fun calculate(ls: List<String>): Double {
            val stack = Stack<String>()
            for (item in ls) {
                if (item.matches("\\d+\\.?\\d*".toRegex())) {
                    stack.push(item)
                }
            }
            for (item in ls) {
                if (item.matches("\\d+\\.?\\d*".toRegex())) {
                } else {
                    val num2 = stack.pop().toDouble()
                    val num1 = stack.pop().toDouble()
                    var res = 0.0
                    if (item == "+") {
                        res = num1 + num2
                    } else if (item == "-") {
                        res = num1 - num2
                        if (res < 0) {
                            res = 0.0
                        }
                    } else if (item == "*") {
                        res = num1 * num2
                    } else if (item == "/") {
                        res = if (java.lang.Double.isNaN(num1 / num2)) {
                            0.0
                        } else if (num1 / num2 == Double.POSITIVE_INFINITY) {
                            0.0
                        } else {
                            num1 / num2
                        }
                        val bg = BigDecimal(res)

                        Log.d("TAGX", "" + bg)
                        res = bg.setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
                    }
                    stack.push("" + res)
                }
            }

            return stack.pop().toDouble()
        }
    }
}
