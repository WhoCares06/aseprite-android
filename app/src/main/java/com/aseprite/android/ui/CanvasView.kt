package com.aseprite.android.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class CanvasView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var bitmap: Bitmap? = null
    private var gridEnabled = true
    private var onionSkinEnabled = false
    private var zoomLevel = 1.0f
    private var panX = 0f
    private var panY = 0f
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val gridPaint = Paint().apply {
        color = Color.parseColor("#4A4A4A")
        strokeWidth = 1f
    }
    private val backgroundPaint = Paint().apply {
        color = Color.parseColor("#3C3C3C")
    }
    private val checkerPaint1 = Paint().apply {
        color = Color.parseColor("#3C3C3C")
    }
    private val checkerPaint2 = Paint().apply {
        color = Color.parseColor("#2A2A2A")
    }
    private val checkerSize = 16f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        val width = width.toFloat()
        val height = height.toFloat()
        
        // Draw checkerboard background (transparency indicator)
        if (bitmap == null) {
            drawCheckerboard(canvas, width, height)
        }
        
        // Draw bitmap if available
        bitmap?.let { bmp ->
            val scaledWidth = bmp.width * zoomLevel
            val scaledHeight = bmp.height * zoomLevel
            
            val left = (width - scaledWidth) / 2 + panX
            val top = (height - scaledHeight) / 2 + panY
            
            val dstRect = Rect(
                left.toInt(),
                top.toInt(),
                (left + scaledWidth).toInt(),
                (top + scaledHeight).toInt()
            )
            
            canvas.drawBitmap(bmp, null, dstRect, paint)
            
            // Draw grid if enabled
            if (gridEnabled && zoomLevel > 0.5f) {
                drawGrid(canvas, left, top, scaledWidth, scaledHeight, bmp.width, bmp.height)
            }
        }
        
        // Draw border
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        paint.color = Color.parseColor("#4A4A4A")
        canvas.drawRect(0f, 0f, width, height, paint)
        paint.style = Paint.Style.FILL
    }
    
    private fun drawCheckerboard(canvas: Canvas, width: Float, height: Float) {
        var y = 0f
        var toggle = false
        while (y < height) {
            var x = 0f
            while (x < width) {
                canvas.drawRect(x, y, x + checkerSize, y + checkerSize, if (toggle) checkerPaint1 else checkerPaint2)
                x += checkerSize
                toggle = !toggle
            }
            y += checkerSize
            toggle = !toggle
        }
    }
    
    private fun drawGrid(canvas: Canvas, left: Float, top: Float, scaledWidth: Float, scaledHeight: Float, imgWidth: Int, imgHeight: Int) {
        val gridSpacing = zoomLevel
        if (gridSpacing < 4f) return // Too zoomed out
        
        var x = left
        while (x < left + scaledWidth) {
            canvas.drawLine(x, top, x, top + scaledHeight, gridPaint)
            x += gridSpacing
        }
        
        var y = top
        while (y < top + scaledHeight) {
            canvas.drawLine(left, y, left + scaledWidth, y, gridPaint)
            y += gridSpacing
        }
    }
    
    fun setBitmap(bmp: Bitmap) {
        bitmap = bmp
        invalidate()
    }
    
    fun getBitmap(): Bitmap? = bitmap
    
    fun toggleGrid() {
        gridEnabled = !gridEnabled
        invalidate()
    }
    
    fun toggleOnionSkin() {
        onionSkinEnabled = !onionSkinEnabled
        invalidate()
    }
    
    fun setZoom(zoom: Float) {
        zoomLevel = zoom.coerceIn(0.1f, 50f)
        invalidate()
    }
    
    fun setPan(x: Float, y: Float) {
        panX = x
        panY = y
        invalidate()
    }
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        // TODO: Handle pan, zoom, drawing
        return super.onTouchEvent(event)
    }
}