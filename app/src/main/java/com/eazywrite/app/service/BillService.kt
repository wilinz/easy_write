package com.eazywrite.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.eazywrite.app.R
import com.eazywrite.app.data.notificationPermission
import com.eazywrite.app.data.repository.BillRepository
import com.eazywrite.app.ui.bill.DesktopAppWidget
import com.eazywrite.app.ui.main.MainActivity
import com.eazywrite.app.ui.notification.NotificationAddBillActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

class BillService : LifecycleService() {

    override fun onCreate() {
        super.onCreate()
        lifecycleScope.launch {
            launch {
                val now = LocalDate.now()
                BillRepository.countBillFlow(startDate = now, endDate = now.plusDays(1)).collect {
                    if (this@BillService.notificationPermission.first()) {
                        updateNotification(createNotification(it))
                    }
                }
            }
            launch {
                BillRepository.getRecentFlow(4).collect {
                    DesktopAppWidget.updateWidgetView(this@BillService, it)
                }
            }
            launch {
                this@BillService.notificationPermission.collect { ok ->
                    if (ok) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            createNotificationChannel() //创建通知频道
                        }
                        startForeground(1, createNotification(0))
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            stopForeground(STOP_FOREGROUND_REMOVE)
                        } else {
                            notificationManager.cancel(1)
                        }
                    }
                }
            }
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    /**
     * 创建通知频道
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        //Android8.0(API26)以上需要
        val importance = NotificationManager.IMPORTANCE_HIGH //重要程度
        val channel = NotificationChannel("简单记账", "快捷记账通知", importance)
        channel.description = "通知栏快捷记账"
        notificationManager.createNotificationChannel(channel) //设置频道
    }

    /**
     * 创建前台通知
     */
    private fun createNotification(todayBillCount: Int): Notification {
        val remoteViews = getRemoteView(todayBillCount)
        //显示在下拉栏中，0为不显示
        return NotificationCompat.Builder(applicationContext, "简单记账")
            .setCustomContentView(remoteViews)
            .setCustomBigContentView(remoteViews)
            .setShowWhen(false)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.ic_launcher_playstore)
            .build()
    }

    private val notificationManager get() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private fun updateNotification(notification: Notification) {
        notificationManager.notify(1, notification)
    }

    private fun getRemoteView(todayBillCount: Int): RemoteViews {
        val remoteViews = RemoteViews(packageName, R.layout.notify_layout)
        val text =
            if (todayBillCount > 0) "今日已记账" + todayBillCount + "笔，请继续坚持哦!" else "今日还没记账哦，快去记一笔吧"
        remoteViews.setTextViewText(R.id.tv_bills_count, text)
        val intentMainActivity = Intent(applicationContext, MainActivity::class.java)
        val flag =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_ONE_SHOT
        val pendingIntentMainActivity = PendingIntent.getActivity(
            applicationContext,
            0,
            intentMainActivity,
            flag
        ) //暂时没搞懂
        remoteViews.setOnClickPendingIntent(R.id.notification_bg, pendingIntentMainActivity)
        val intentNotificationAddBillActivity =
            Intent(applicationContext, NotificationAddBillActivity::class.java)
        val pendingIntentNotificationAddBillActivity = PendingIntent.getActivity(
            applicationContext, 0, intentNotificationAddBillActivity, PendingIntent.FLAG_IMMUTABLE
        ) //暂时没搞懂
        remoteViews.setOnClickPendingIntent(
            R.id.add_bill,
            pendingIntentNotificationAddBillActivity
        )
        return remoteViews
    }
}