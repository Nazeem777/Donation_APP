package com.donorpath.app.di

import android.content.Context
import com.donorpath.app.data.local.AppDatabase
import com.donorpath.app.data.repository.DonorPathRepository

class AppContainer(context: Context) {
    private val database = AppDatabase.getInstance(context)
    val repository = DonorPathRepository(database)
}
