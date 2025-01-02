package com.eazywrite.app.ui.bill

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import com.eazywrite.app.R
import com.eazywrite.app.data.model.Bill
import com.eazywrite.app.data.repository.BillRepository
import com.eazywrite.app.ui.main.MainActivity
import com.eazywrite.app.util.truncateString
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 桌面小组件
 *
 * @author Admin
 */
class DesktopAppWidget : AppWidgetProvider() {
    /**
     * 每次窗口小部件被更新都调用一次该方法（创建、时间到更新周期都会调起这里）
     */
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        //更新数据
        update(context)
    }

    fun update(context: Context){
        kotlin.runCatching {
            context.startService(Intent(context, UpdateAppWidgetService::class.java))
        }.onFailure {
            MainScope().launch {
                Log.d("TAG", "onStartCommand:")
                val recentBill = BillRepository.getRecent(4)
                updateWidgetView(context, recentBill)
            }
        }
    }
    /**
     * 接收窗口小部件点击时发送的广播
     */
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
    }

    /**
     * 每删除一次窗口小部件就调用一次
     */
    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
    }

    /**
     * 当最后一个该窗口小部件删除时调用该方法
     */
    override fun onDisabled(context: Context) {
        super.onDisabled(context)
    }

    /**
     * 当该窗口小部件第一次添加到桌面时调用该方法
     */
    override fun onEnabled(context: Context) {
        super.onEnabled(context)
    }

    /**
     * 当小部件大小改变时
     */
    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
    }

    /**
     * 当小部件从备份恢复时调用该方法
     */
    override fun onRestored(context: Context, oldWidgetIds: IntArray, newWidgetIds: IntArray) {
        super.onRestored(context, oldWidgetIds, newWidgetIds)
    }

    companion object {
        /**
         * 更新桌面小组件数据用，APP中也可以在任意地方传入任意数据进来主动更新小组件数据
         */
        fun updateWidgetView(context: Context, recentBill: List<Bill>) {
            //初始化RemoteViews
            val componentName = ComponentName(context, DesktopAppWidget::class.java)
            val remoteViews = RemoteViews(context.packageName, R.layout.widget_test)

            val flag =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_ONE_SHOT

            val pendingIntent: PendingIntent = PendingIntent.getActivity(
                context,
                0,
                Intent(context, MainActivity::class.java),
                flag
            )
            val pendingIntent1: PendingIntent = PendingIntent.getActivity(
                context,
                0,
                Intent(context, AddBillContentActivity::class.java),
                flag
            )

            val pendingIntent2: PendingIntent = PendingIntent.getService(
                context,
                0,
                Intent(context, UpdateAppWidgetService::class.java),
                flag
            )

            remoteViews.setOnClickPendingIntent(R.id.llt_more, pendingIntent)
            remoteViews.setOnClickPendingIntent(R.id.relativelayout_edit, pendingIntent1)
            remoteViews.setOnClickPendingIntent(R.id.refresh, pendingIntent2)
            remoteViews.setOnClickPendingIntent(R.id.iv_title, pendingIntent2)

            val dateFormat = DateTimeFormatter.ofPattern("yyyy年MM月")
            val now = LocalDateTime.now()


            //更新文本数据
            remoteViews.setTextViewText(R.id.tv_day, now.dayOfMonth.toString())
            remoteViews.setTextViewText(R.id.tv_monthyear, now.format(dateFormat))
            remoteViews.setTextViewText(R.id.tv_week, weekDayMap[now.dayOfWeek.value].toString())

            val billNameViewIds = listOf(R.id.xiangmu1, R.id.xiangmu2, R.id.xiangmu3, R.id.xiangmu4)
            val billAmountViewIds = listOf(R.id.money1, R.id.money2, R.id.money3, R.id.money4)



            recentBill.forEachIndexed { index, bill ->
                var prefix = if (bill.type == Bill.TYPE_OUT) "-" else "+"
                if (bill.amount == 0.toBigDecimal()) prefix = ""
                val amount = prefix + String.format(
                    "%.2f",
                    bill.amount
                )

                remoteViews.setTextViewText(billNameViewIds[index], bill.name.truncateString(6))
                remoteViews.setTextViewText(billAmountViewIds[index], amount)
            }
            if (recentBill.size < 4) {
                for (index in recentBill.size until 4) {
                    remoteViews.setTextViewText(billNameViewIds[index], "")
                    remoteViews.setTextViewText(billAmountViewIds[index], "")
                }
            }
            if (recentBill.isEmpty()){
                remoteViews.setTextViewText(billNameViewIds[0], "空空如也")
                remoteViews.setTextViewText(billAmountViewIds[0], "")
            }
            //开始更新视图
            val awm = AppWidgetManager.getInstance(context)
            awm.updateAppWidget(componentName, remoteViews)
        }
    }

}

val weekDayMap =
    mapOf(1 to "星期一", 2 to "星期二", 3 to "星期三", 4 to "星期四", 5 to "星期五", 6 to "星期六", 7 to "星期日")