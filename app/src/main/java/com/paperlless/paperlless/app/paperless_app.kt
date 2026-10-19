package com.paperless.app

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader

class PaperlessApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // تهيئة AdMob
        MobileAds.initialize(this) {}
        // تهيئة PDFBox لاستخدامه في تعديل PDF
        PDFBoxResourceLoader.init(applicationContext)
    }
}