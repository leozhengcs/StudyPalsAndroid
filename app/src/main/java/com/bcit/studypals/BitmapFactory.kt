package com.bcit.studypals
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun getDrawableWidth(resourceId: Int): Int {
    val context = LocalContext.current
    val bitmap = remember {
        BitmapFactory.decodeResource(context.resources, resourceId)
    }
    return bitmap.width
}
