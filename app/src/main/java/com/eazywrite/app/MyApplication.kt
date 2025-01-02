package com.eazywrite.app

import android.app.Application
import com.tencent.bugly.crashreport.CrashReport
import org.litepal.LitePal

class MyApplication : Application() {

    companion object {
        lateinit var instance: MyApplication

    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        CrashReport.initCrashReport(this, "581128a204", BuildConfig.DEBUG);
        LitePal.initialize(this)
    }
}