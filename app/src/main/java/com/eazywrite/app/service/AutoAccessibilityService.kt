package com.eazywrite.app.service

import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import com.wilinz.accessbilityx.AccessibilityxService

class AutoAccessibilityService : AccessibilityxService() {

    companion object {
        var instance: AutoAccessibilityService? = null
            private set
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Toast.makeText(this, "无障碍已打开", Toast.LENGTH_SHORT).show()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        super.onAccessibilityEvent(event)
    }


    override fun onInterrupt() {
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }
}