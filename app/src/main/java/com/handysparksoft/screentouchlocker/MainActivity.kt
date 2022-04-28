package com.handysparksoft.screentouchlocker

import android.content.ContextWrapper
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.handysparksoft.screentouchlocker.ui.theme.ScreenTouchLockerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val canDrawOverlays by remember { mutableStateOf(drawOverOtherAppsEnabled()) }
            ScreenTouchLockerTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
                    MainScreen(canDrawOverlays = canDrawOverlays)
                }
            }
        }
    }
}

@Composable
fun MainScreen(canDrawOverlays: Boolean) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        Text(
            text = "Screen Locker App",
            style = MaterialTheme.typography.h4,
            fontWeight = Bold,
            modifier = Modifier.align(CenterHorizontally)
        )
        Spacer(modifier = Modifier.weight(1f))
        if (!canDrawOverlays) {
            Text(text = "You might need to grant permissions for ScreenTouchLocker app to work correctly.")
        }
        Button(
            modifier = Modifier.align(CenterHorizontally),
            onClick = {
                ScreenTouchLockerService.startTheService(context = context, action = ScreenTouchLockerAction.ActionLock)
                if (context.drawOverOtherAppsEnabled()) {
                    ShakeDetectorService.startTheService(context = context)
                    (context as ComponentActivity).finish()
                }
                (context as ContextWrapper).logdAndToast("Clicked!")
            },
        ) {
            Text(text = "Start locker")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ScreenTouchLockerTheme {
        MainScreen(false)
    }
}
