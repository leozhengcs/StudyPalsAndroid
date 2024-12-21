package com.bcit.studypals.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.os.Handler
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.bcit.studypals.R

class AnimatedSpriteView(context: Context) : SurfaceView(context), Runnable {

    private var thread: Thread? = null
    private var isPlaying = false
    private val paint = Paint()
    private var spriteSheet: Bitmap =
        BitmapFactory.decodeResource(resources, R.drawable.pet_fox) // Load sprite sheet

    // Sprite properties
    private val frameWidth = 32  // Width of a single frame
    private val frameHeight = 32 // Height of a single frame
    private val frameCount = 6    // Number of frames in the sprite sheet
    private var currentFrame = 0  // The current frame index
    private val frameDuration = 200L // Duration of each frame in milliseconds

    private var lastFrameChangeTime = System.currentTimeMillis()

    override fun run() {
        while (isPlaying) {
            if (System.currentTimeMillis() - lastFrameChangeTime >= frameDuration) {
                updateFrame()
                lastFrameChangeTime = System.currentTimeMillis()
            }
            draw()
        }
    }

    private fun updateFrame() {
        currentFrame = (currentFrame + 1) % frameCount
    }

    private fun draw() {
        if (holder.surface.isValid) {
            val canvas: Canvas = holder.lockCanvas()

            // Clear the screen
            canvas.drawColor(android.graphics.Color.BLACK)

            // Calculate source and destination rectangles
            val srcX = currentFrame * frameWidth
            val srcRect = android.graphics.Rect(srcX, 0, srcX + frameWidth, frameHeight)
            val destRect = android.graphics.Rect(100, 100, 200, 200) // Position on the screen

            // Draw the current frame
            canvas.drawBitmap(spriteSheet, srcRect, destRect, paint)

            holder.unlockCanvasAndPost(canvas)
        }
    }

    fun pause() {
        isPlaying = false
        thread?.join()
    }

    fun resume() {
        isPlaying = true
        thread = Thread(this)
        thread?.start()
    }
}

@Composable
fun AnimatedSprite(
    spriteSheetResId: Int,
    frameWidth: Int,
    frameHeight: Int,
    frameCount: Int,
    durationMillis: Int,
    startRow: Int = 0, // Specify the row you want to start from
    modifier: Modifier = Modifier
) {
    // Load the sprite sheet as a Bitmap
    val options = BitmapFactory.Options().apply {
        inScaled = false // Disable automatic scaling
    }
    val spriteSheetBitmap = BitmapFactory.decodeResource(LocalContext.current.resources, spriteSheetResId, options)

    // Calculate the number of frames per row in the sprite sheet
    val framesPerRow = spriteSheetBitmap.width / frameWidth

    // Pre-calculate and cache frames
    val frames = remember(spriteSheetBitmap, frameWidth, frameHeight, frameCount, startRow) {
        (0 until frameCount).map { frameIndex ->
            val srcX = (frameIndex % framesPerRow) * frameWidth
            val srcY = (startRow * frameHeight) + (frameIndex / framesPerRow) * frameHeight
            Bitmap.createBitmap(spriteSheetBitmap, srcX, srcY, frameWidth, frameHeight).asImageBitmap()
        }
    }

    // Animate through the preloaded frames
    val transition = rememberInfiniteTransition(label = "Sprite Animation")
    val currentFrame: androidx.compose.runtime.State<Int> = transition.animateValue(
        initialValue = 0,
        targetValue = frameCount - 1,
        typeConverter = Int.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "CurrentFrame"
    )

    // Render the current frame
    Image(
        bitmap = frames[currentFrame.value],
        contentDescription = "Animated Sprite",
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}
