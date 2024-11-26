package com.bcit.studypals

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
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
fun Home(navController: NavController, background: Background, studying: Boolean = false) {
    val (foregroundId, skyId, backdropId) = background

    val aspectRatio = 576f/324f
    // Obtain the density to convert pixels to dp
    val density = LocalDensity.current
    val imageWidthPx = getDrawableWidth(skyId).toFloat()
    val imageWidthDp = with(density) { (imageWidthPx * aspectRatio).toDp() }

    val infiniteTransition = rememberInfiniteTransition(label = "Parallax Transition")
    val skyOffsetX = createSeamlessParallaxAnimation(infiniteTransition, imageWidthDp, durationMillis = 20000)
    val backdropOffsetX = createSeamlessParallaxAnimation(infiniteTransition, imageWidthDp, durationMillis = 15000)
    val foregroundOffsetX = createSeamlessParallaxAnimation(infiniteTransition, imageWidthDp, durationMillis = 10000)

    Column (
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(600.dp)
        ) {
            if (studying) {
                SeamlessImageLayer(skyId, skyOffsetX.value, imageWidthDp, aspectRatio)
                SeamlessImageLayer(backdropId, backdropOffsetX.value, imageWidthDp, aspectRatio)
                SeamlessImageLayer(foregroundId, foregroundOffsetX.value, imageWidthDp, aspectRatio)

                // Second set of images shifted by imageWidthDp to create seamless loop
                SeamlessImageLayer(skyId, skyOffsetX.value + imageWidthDp, imageWidthDp, aspectRatio)
                SeamlessImageLayer(backdropId, backdropOffsetX.value + imageWidthDp, imageWidthDp, aspectRatio)
                SeamlessImageLayer(foregroundId, foregroundOffsetX.value + imageWidthDp, imageWidthDp, aspectRatio)
            } else {
                Image(
                    painter = painterResource(skyId),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(aspectRatio)
                        .scale(SCALE),
                    contentScale = ContentScale.FillHeight
                )
                Image(
                    painter = painterResource(backdropId),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(aspectRatio)
                        .scale(SCALE),
                    contentScale = ContentScale.FillHeight
                )
                Image(
                    painter = painterResource(foregroundId),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(aspectRatio)
                        .scale(SCALE),
                    contentScale = ContentScale.FillHeight
                )
            }
        }
    }


}

@Composable
fun createSeamlessParallaxAnimation(
    transition: InfiniteTransition,
    imageWidthDp: Dp,
    durationMillis: Int
): State<Dp> {
    return transition.animateValue(
        initialValue = 0.dp,
        targetValue = imageWidthDp,
        typeConverter = Dp.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "SeamlessParallaxAnimation"
    )
}

@Composable
fun SeamlessImageLayer(imageId: Int, offsetXValue: Dp, imageWidthDp: Dp, aspectRatio: Float) {
    val density = LocalDensity.current

    // Convert Dp to Px for the modulus operation
    val seamlessOffsetPx = with(density) { offsetXValue.toPx() % imageWidthDp.toPx() }

    // Convert back to Dp for use in Modifier
    val seamlessOffsetDp = with(density) { seamlessOffsetPx.toDp() }

    // Render the first and second images for seamless looping
    Image(
        painter = painterResource(imageId),
        contentDescription = null,
        modifier = Modifier
            .fillMaxHeight()
            .offset(x = -seamlessOffsetDp)
            .aspectRatio(aspectRatio)
            .scale(SCALE),
        contentScale = ContentScale.FillHeight
    )
    Image(
        painter = painterResource(imageId),
        contentDescription = null,
        modifier = Modifier
            .fillMaxHeight()
            .offset(x = -seamlessOffsetDp + imageWidthDp)
            .aspectRatio(aspectRatio)
            .scale(SCALE),
        contentScale = ContentScale.FillHeight
    )
}

