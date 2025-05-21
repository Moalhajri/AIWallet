package com.example.walletai

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WalletAIApplication : Application() {
    companion object {
        lateinit var instance: WalletAIApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}