package com.bcit.studypals

import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.bcit.studypals.ui.Background
import com.bcit.studypals.ui.components.AnimatedSprite
import com.bcit.studypals.data.state.UserState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

const val SCALE = 3f

@Composable
fun Home(navController: NavController, background: Background) {
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

    val firebaseAuth = FirebaseAuth.getInstance()
    val currentUser = firebaseAuth.currentUser
    val userId = currentUser?.uid

    val db = FirebaseFirestore.getInstance()

    val userState = ViewModelProvider(navController.getBackStackEntry("home"))[UserState::class.java]
    val isStudying by userState.studying.observeAsState(initial = false)
    val scrollState = rememberScrollState()

    // TODO: Refactor to work with the Firebase State
    if (userId != null) {
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val userData = document.data // Retrieve the data as a Map
                    Log.d("Firestore", "User data: $userData")

                    // Example: Extract specific fields
                    val name = document.getString("name")
                    val email = document.getString("email")
                    val points = document.getLong("points") ?: 0
                    val currentPet = document.getString("current_pet") ?: "fox"
                    val studyHours = document.getLong("study_hours") ?: 0

                    Log.d("Firestore", "Name: $name, Email: $email, Points: $points")
                } else {
                    Log.d("Firestore", "No such user document")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting user document", exception)
            }
    } else {
        Log.d("Firestore", "User is not logged in.")
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // The display of the pet and background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(600.dp)
        ) {
            if (isStudying) {
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

            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y=5.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                AnimatedSprite(
                    spriteSheetResId = R.drawable.pet_fox, // Replace with your sprite sheet resource
                    frameWidth = 320, // Replace with frame width in px
                    frameHeight = 320, // Replace with frame height in px
                    frameCount = 4,  // Replace with the total number of frames
                    startRow = if (isStudying) 1 else 0,
                    durationMillis = 1000, // Frame duration
                    modifier = Modifier
                        .size(160.dp)
                )
            }

        }
        Spacer(modifier = Modifier.height(75.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = {
                    userState.setStudying(!isStudying)
                }
            ) {
                if (isStudying) {
                    Text("End Session")
                } else {
                    Text("Start Study Session")
                }
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

