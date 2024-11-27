package com.bcit.studypals.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.os.Handler
import com.bcit.studypals.R

class AnimatedSpriteView(context: Context) : SurfaceView(context), Runnable {

    private var thread: Thread? = null
    private var isPlaying = false
    private val paint = Paint()
    private lateinit var spriteSheet: Bitmap

    // Sprite properties
    private val frameWidth = 32  // Width of a single frame
    private val frameHeight = 32 // Height of a single frame
    private val frameCount = 6    // Number of frames in the sprite sheet
    private var currentFrame = 0  // The current frame index
    private val frameDuration = 200L // Duration of each frame in milliseconds

    private var lastFrameChangeTime = System.currentTimeMillis()

    init {
        spriteSheet = BitmapFactory.decodeResource(resources, R.drawable.pet_fox) // Load sprite sheet
    }

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