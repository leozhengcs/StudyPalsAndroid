package com.bcit.studypals

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bcit.studypals.ui.Background

const val SCALE = 3f

@Composable
fun Home(navController: NavController, background: Background) {
    val (foregroundId, skyId, backdropId) = background

    val aspectRatio = 576f/324f
    // Obtain the density to convert pixels to dp
    val density = LocalDensity.current
    val imageWidthPx = getDrawableWidth(skyId).toFloat()
    val imageWidthDp = with(density) { (imageWidthPx * aspectRatio).toDp() }

    // Sliding animation using Dp values
    val infiniteTransition = rememberInfiniteTransition()
    val offsetX = infiniteTransition.animateValue(
        initialValue = 0.dp,
        targetValue = -imageWidthDp,
        typeConverter = Dp.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 10000, // Adjust the duration as needed
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = ""
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(600.dp)
        ) {
            // First set of images
            ImageLayer(skyId, offsetX.value, aspectRatio)
            ImageLayer(backdropId, offsetX.value, aspectRatio)
            ImageLayer(foregroundId, offsetX.value, aspectRatio)

            // Second set of images shifted by imageWidthDp to create seamless loop
            ImageLayer(skyId, offsetX.value + imageWidthDp, aspectRatio)
            ImageLayer(backdropId, offsetX.value + imageWidthDp, aspectRatio)
            ImageLayer(foregroundId, offsetX.value + imageWidthDp, aspectRatio)
        }
    }
}

@Composable
fun ImageLayer(imageId: Int, offsetXValue: Dp, aspectRatio: Float) {
    Image(
        painter = painterResource(imageId),
        contentDescription = null,
        modifier = Modifier
            .fillMaxHeight()
            .offset(x = offsetXValue)
            .aspectRatio(aspectRatio)
            .scale(SCALE),
        contentScale = ContentScale.FillHeight
    )
}


