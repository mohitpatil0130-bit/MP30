package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.HostelMainScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.HostelViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val hostelViewModel: HostelViewModel = viewModel()
                HostelMainScreen(viewModel = hostelViewModel)
            }
        }
    }
}
