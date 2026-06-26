package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.navigation.AppNavigation
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MusicViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MusicViewModel = viewModel()
            val appTheme by viewModel.appTheme.collectAsState()
            
            MyApplicationTheme(theme = appTheme) {
                AppNavigation(viewModel = viewModel)
            }
        }
    }
}
