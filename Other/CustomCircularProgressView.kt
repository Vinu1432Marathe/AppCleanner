package com.software.app.update.smart.Other

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View

class CustomCircularProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    var showSubText: Boolean = true

    private val strokeWidth = 20f
    private val glowWidth = 40f

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#F1EDFF")
        style = Paint.Style.STROKE
        strokeWidth = this@CustomCircularProgressView.strokeWidth
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#7A5AF8")
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = this@CustomCircularProgressView.strokeWidth
    }

    private val outerGlowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#7A5AF8")
        style = Paint.Style.STROKE
        strokeWidth = glowWidth
        alpha = 25
        setShadowLayer(25f, 0f, 0f, Color.parseColor("#7A5AF8"))
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1C1C1E")
        textSize = 80f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    private val subTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GRAY
        textSize = 32f
        textAlign = Paint.Align.CENTER
    }

    private var animatedProgress = 0f
    private var centerText: String = ""
    private var subText: String = ""

    fun setProgressAnimated(progress: Int, duration: Long = 1000L) {
        val animator = ValueAnimator.ofFloat(0f, progress.toFloat())
        animator.duration = duration
        animator.addUpdateListener {
            animatedProgress = it.animatedValue as Float
            invalidate()
        }
        animator.start()
    }

    fun setCenterText(text: String) {
        centerText = text
        invalidate()
    }

    fun setSubText(text: String) {
        subText = text
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        setLayerType(LAYER_TYPE_SOFTWARE, null)

        val radius = (width.coerceAtMost(height) / 2 * 0.8).toFloat()
        val centerX = width / 2f
        val centerY = height / 2f
        val rect = RectF(
            centerX - radius, centerY - radius,
            centerX + radius, centerY + radius
        )

        // Background ring
        canvas.drawArc(rect, 0f, 360f, false, backgroundPaint)

        // Outer glow
        canvas.drawArc(rect, -90f, 360 * (animatedProgress / 100f), false, outerGlowPaint)

        // Progress arc
        canvas.drawArc(rect, -90f, 360 * (animatedProgress / 100f), false, progressPaint)

        // Center text
        canvas.drawText(centerText.ifEmpty { "${animatedProgress.toInt()}%" }, centerX, centerY + 20f, textPaint)

        // Sub text
        if (showSubText) {
            canvas.drawText(subText, centerX, centerY + 60f, subTextPaint)
        }
    }
}


//
//class CustomCircularProgressView @JvmOverloads constructor(
//    context: Context,
//    attrs: AttributeSet? = null
//) : View(context, attrs) {
//
//    var showSubText: Boolean = true
//
//    private val strokeWidth = 20f
//    private val glowWidth = 40f
//
//    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
//        color = Color.parseColor("#F1EDFF") // Soft glow background
//        style = Paint.Style.STROKE
//        strokeWidth = this@CustomCircularProgressView.strokeWidth
//    }
//
//    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
//        color = Color.parseColor("#7A5AF8") // Purple progress
//        style = Paint.Style.STROKE
//        strokeCap = Paint.Cap.ROUND
//        strokeWidth = this@CustomCircularProgressView.strokeWidth
//    }
//
//    private val outerGlowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
//        color = Color.parseColor("#7A5AF8")
//        style = Paint.Style.STROKE
//        strokeWidth = glowWidth
//        alpha = 25 // Subtle glow
//        setShadowLayer(25f, 0f, 0f, Color.parseColor("#7A5AF8"))
//    }
//
//    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
//        color = Color.parseColor("#1C1C1E")
//        textSize = 80f
//        textAlign = Paint.Align.CENTER
//        typeface = Typeface.DEFAULT_BOLD
//    }
//
//    private val subTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
//        color = Color.GRAY
//        textSize = 32f
//        textAlign = Paint.Align.CENTER
//    }
//
//    private var animatedProgress = 0f
//    private var targetProgress = 0
//
//    fun setProgressAnimated(progress: Int, duration: Long = 1000L) {
//        targetProgress = progress
//        val animator = ValueAnimator.ofFloat(0f, progress.toFloat())
//        animator.duration = duration
//        animator.addUpdateListener {
//            animatedProgress = it.animatedValue as Float
//            invalidate()
//        }
//        animator.start()
//    }
//
//    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
//        setLayerType(LAYER_TYPE_SOFTWARE, null)
//
//        val radius = (width.coerceAtMost(height) / 2 * 0.8).toFloat()
//        val centerX = width / 2f
//        val centerY = height / 2f
//        val rect = RectF(
//            centerX - radius, centerY - radius,
//            centerX + radius, centerY + radius
//        )
//
//        // 1. Draw full background circle
//        canvas.drawArc(rect, 0f, 360f, false, backgroundPaint)
//
//        // 2. Draw glowing outer arc
//        canvas.drawArc(rect, -90f, 360 * (animatedProgress / 100f), false, outerGlowPaint)
//
//        // 3. Draw main progress arc
//        canvas.drawArc(rect, -90f, 360 * (animatedProgress / 100f), false, progressPaint)
//
//        // 4. Draw text (centered percentage)
//        canvas.drawText("${animatedProgress.toInt()}%", centerX, centerY + 20f, textPaint)
//
//        if (showSubText) {
//            canvas.drawText("optimize", centerX, centerY + 60f, subTextPaint)
//        }
//    }
//
//
////    override fun onDraw(canvas: Canvas) {
////        super.onDraw(canvas)
////
////        val size = width.coerceAtMost(height).toFloat()
////        val centerX = width / 2f
////        val centerY = height / 2f
////        val radius = size / 2 - glowWidth
////
////        val rect = RectF(
////            centerX - radius, centerY - radius,
////            centerX + radius, centerY + radius
////        )
////
////        setLayerType(LAYER_TYPE_SOFTWARE, null) // Enable shadow
////
////        // Outer glow
////        canvas.drawArc(rect, -90f, 360f, false, outerGlowPaint)
////
////        // Background arc
////        canvas.drawArc(rect, 0f, 360f, false, backgroundPaint)
////
////        // Progress arc
////        canvas.drawArc(rect, -90f, 360 * (animatedProgress / 100f), false, progressPaint)
////
////        // Thumb (moving dot at the end)
////        if (animatedProgress > 0f) {
////            val angle = Math.toRadians((360 * animatedProgress / 100f - 90).toDouble())
////            val thumbX = (centerX + radius * cos(angle)).toFloat()
////            val thumbY = (centerY + radius * sin(angle)).toFloat()
////
////            val thumbPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
////                color = Color.parseColor("#7A5AF8")
////                style = Paint.Style.FILL
////            }
////            canvas.drawCircle(thumbX, thumbY, 10f, thumbPaint)
////        }
////
////        // Percentage text
////        canvas.drawText("${animatedProgress.toInt()}%", centerX, centerY + 20f, textPaint)
////
////        // Sub text
////        if (showSubText) {
////            canvas.drawText("Optimize", centerX, centerY + 80f, subTextPaint)
////        }
////    }
//}