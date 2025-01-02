package com.eazywrite.app.ui.bill

import android.content.Intent
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.eazywrite.app.common.toast
import com.eazywrite.app.data.repository.BillRepository
import kotlinx.coroutines.launch

class UpdateAppWidgetService : LifecycleService() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        lifecycleScope.launch {
            Log.d("TAG", "onStartCommand:")
            val recentBill = BillRepository.getRecent(4)
            DesktopAppWidget.updateWidgetView(this@UpdateAppWidgetService, recentBill)
            stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }

}