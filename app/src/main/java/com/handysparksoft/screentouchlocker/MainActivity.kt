package com.handysparksoft.screentouchlocker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.handysparksoft.screentouchlocker.ui.onboarding.OnboardingScreen
import com.handysparksoft.screentouchlocker.ui.theme.ScreenTouchLockerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val canDrawOverlays by remember { mutableStateOf(drawOverOtherAppsEnabled()) }
            ScreenTouchLockerTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
                    OnboardingScreen(canDrawOverlays = canDrawOverlays)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ScreenTouchLockerTheme {
        OnboardingScreen(false)
    }
}
