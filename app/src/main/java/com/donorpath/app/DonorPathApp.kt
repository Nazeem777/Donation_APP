package com.donorpath.app

import android.app.Application
import com.donorpath.app.di.AppContainer

class DonorPathApp : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
