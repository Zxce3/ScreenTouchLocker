package com.handysparksoft.screentouchlocker

import android.app.Activity
import android.content.Context
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
import com.google.android.play.core.review.ReviewManagerFactory
import com.handysparksoft.screentouchlocker.platform.InAppReviewManager
import com.handysparksoft.screentouchlocker.ui.onboarding.OnboardingScreen
import com.handysparksoft.screentouchlocker.ui.theme.ScreenTouchLockerTheme
import kotlin.random.Random

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (intent.action == ScreenTouchLockerAction.ActionUnlock.toString()) {
            ScreenTouchLockerService.startTheService(context = this, action = ScreenTouchLockerAction.ActionUnlock)
        }

        startInAppReviewFlow(this)

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

private fun startInAppReviewFlow(context: Context) {
    // InAppReview request
    val askForAReview = Random.nextInt(3) == 1
    if (askForAReview) {
        (context as? Activity)?.let { activity ->
            InAppReviewManager(ReviewManagerFactory.create(context)).requestReviewFlow(activity)
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
