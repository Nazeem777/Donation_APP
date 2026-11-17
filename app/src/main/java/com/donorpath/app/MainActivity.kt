package com.donorpath.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.donorpath.app.ui.navigation.DonorPathNavHost
import com.donorpath.app.ui.theme.DonorPathTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = (application as DonorPathApp).container.repository

        setContent {
            DonorPathTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    DonorPathNavHost(repository = repository)
                }
            }
        }
    }
}
