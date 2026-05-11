package com.example.dimpay

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.FragmentActivity
import com.example.dimpay.auth.AuthGateScreen
import com.example.dimpay.auth.AuthGateViewModel
import com.example.dimpay.core.designsystem.theme.DimPayTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    private val viewModel: AuthGateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DimPayTheme {
                val state by viewModel.uiState.collectAsState()

                AuthGateScreen(
                    state = state,
                    onAuthenticate = { viewModel.authenticate(this) },
                    onSetupSecurity = {
                        val intent = Intent(Settings.ACTION_SECURITY_SETTINGS)
                        startActivity(intent)
                    }
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.checkAuth()
    }

    override fun onStop() {
        super.onStop()
        viewModel.onAppBackgrounded()
    }
}