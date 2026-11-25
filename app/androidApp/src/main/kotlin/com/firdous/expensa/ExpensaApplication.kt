package com.firdous.expensa

import android.app.Application
import com.firdous.expensa.di.initKoinAndroid

class ExpensaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoinAndroid(this)
    }
}
