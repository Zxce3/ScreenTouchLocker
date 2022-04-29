package com.handysparksoft.screentouchlocker.ui.onboarding

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import com.handysparksoft.screentouchlocker.R
import com.handysparksoft.screentouchlocker.ScreenTouchLockerAction
import com.handysparksoft.screentouchlocker.ScreenTouchLockerService
import com.handysparksoft.screentouchlocker.ShakeDetectorService
import com.handysparksoft.screentouchlocker.drawOverOtherAppsEnabled
import com.handysparksoft.screentouchlocker.logdAndToast
import com.handysparksoft.screentouchlocker.ui.theme.ScreenTouchLockerTheme
import com.handysparksoft.screentouchlocker.ui.theme.White90

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnboardingScreen(canDrawOverlays: Boolean) {
    val context = LocalContext.current
    val pagerState = rememberPagerState()

    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundImage(pagerState)

        GradientWhiteBox()

        Column {
            OnboardingContentPager(pagerState)

            HorizontalPagerIndicator(
                pagerState = pagerState,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        Box(Modifier.align(Alignment.BottomCenter)) {
            LockActionContent(canDrawOverlays, context)
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun BackgroundImage(pagerState: PagerState) {
    Crossfade(
        targetState = pagerState.currentPage,
        animationSpec = tween(CrossFadeDuration)
    ) { page ->
        Image(
            painter = painterResource(id = OnboardingImages[page]),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun GradientWhiteBox() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, White90, Color.White, Color.White)
                )
            )
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun OnboardingContentPager(pagerState: PagerState) {
    HorizontalPager(
        count = OnboardingImages.size,
        state = pagerState
    ) { page ->
        val animatedModifier = getAnimatedGraphicsLayer(calculateCurrentOffsetForPage(page))

        PagerSimpleItem(
            page = page,
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .padding(top = 100.dp)
                .defaultMinSize(minHeight = 220.dp)
                .then(animatedModifier)
        )
    }
}

@Composable
private fun LockActionContent(canDrawOverlays: Boolean, context: Context) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 60.dp),
    ) {
        if (!canDrawOverlays) {
            Text(
                text = stringResource(R.string.required_overlay_permissions),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.fillMaxWidth(0.75f)
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .align(Alignment.CenterHorizontally),
            onClick = {
                ScreenTouchLockerService.startTheService(context = context, action = ScreenTouchLockerAction.ActionLock)
                if (context.drawOverOtherAppsEnabled()) {
                    ShakeDetectorService.startTheService(context = context)
                    (context as ComponentActivity).finish()
                }
                (context as ContextWrapper).logdAndToast("Clicked!")
            },
        ) {
            Text(text = stringResource(R.string.lock_the_screen))
        }
    }
}

@Composable
fun PagerSimpleItem(page: Int, modifier: Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(id = OnboardingIcons[page]), contentDescription = null)
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(id = OnboardingTitles[page]),
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = OnboardingSubtitles[page]),
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Center
        )
    }
}

private val OnboardingImages = listOf(
    R.drawable.onboarding1,
    R.drawable.onboarding2,
    R.drawable.onboarding3
)

private val OnboardingIcons = listOf(
    R.drawable.ic_screen_locker_icon,
    R.drawable.ic_shake,
    R.drawable.ic_app_settings
)

private val OnboardingTitles = listOf(
    R.string.onboarding_title_1,
    R.string.onboarding_title_2,
    R.string.onboarding_title_3
)

private val OnboardingSubtitles = listOf(
    R.string.onboarding_subtitle_1,
    R.string.onboarding_subtitle_2,
    R.string.onboarding_subtitle_3
)

private const val CrossFadeDuration = 1000

private fun getAnimatedGraphicsLayer(offsetForPage: Float) = Modifier.graphicsLayer {
    // We use the absolute value which allows us to mirror
    // any effects for both directions

    // We animate the scaleX + scaleY, between 85% and 100%
    lerp(
        start = 0.5.dp,
        stop = 1.dp,
        fraction = 1f - offsetForPage.coerceIn(0f, 1f)
    ).also { scale ->
        scaleX = scale.value
        scaleY = scale.value
    }

    // We animate the alpha, between 50% and 100%
    alpha = lerp(
        start = 0.dp,
        stop = 1.dp,
        fraction = 1f - offsetForPage.coerceIn(0f, 1f)
    ).value
}

@Preview
@Composable
fun OnboardingScreen() {
    ScreenTouchLockerTheme {
        OnboardingScreen(canDrawOverlays = true)
    }
}
