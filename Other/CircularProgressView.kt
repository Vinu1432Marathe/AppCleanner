package com.software.app.update.smart.Other

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.software.app.update.smart.R

class CircularProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    var showSubText: Boolean = true
    private var subText: String = ""

//    fun setCenterText(text: String) {
//        centerText = text
//        invalidate()
//    }

    fun setSubText(text: String) {
        subText = text
        invalidate()
    }
    private val strokeWidth = 40f
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.LTGRAY
        style = Paint.Style.STROKE
        strokeWidth = this@CircularProgressView.strokeWidth
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#A259FF")
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = this@CircularProgressView.strokeWidth
    }

    private fun resolveThemeColor(context: Context, attr: Int): Int {
        val typedValue = TypedValue()
        val theme = context.theme
        theme.resolveAttribute(attr, typedValue, true)
        return ContextCompat.getColor(context, typedValue.resourceId)
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
//        color = Color.parseColor("#A259FF")
        color = resolveThemeColor(context, R.attr.text_color)
        textSize = 70f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
        typeface = ResourcesCompat.getFont(context, R.font.bold)
    }

    private val subTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.DKGRAY
        textSize = 30f
        textAlign = Paint.Align.CENTER
        typeface = ResourcesCompat.getFont(context, R.font.medium)
    }

    private var animatedProgress = 0f
    private var targetProgress = 0

    fun setProgressAnimated(progress: Int, duration: Long = 1000L) {
        targetProgress = progress
        val animator = ValueAnimator.ofFloat(0f, progress.toFloat())
        animator.duration = duration
        animator.addUpdateListener {
            animatedProgress = it.animatedValue as Float
            invalidate()
        }
        animator.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val radius = (width.coerceAtMost(height) / 2 * 0.8).toFloat()
        val centerX = width / 2f
        val centerY = height / 2f
        val rect = RectF(
            centerX - radius, centerY - radius,
            centerX + radius, centerY + radius
        )

        // Background circle
        canvas.drawArc(rect, 0f, 360f, false, backgroundPaint)

        // Progress arc
        canvas.drawArc(rect, -90f, 360 * (animatedProgress / 100f), false, progressPaint)

        // Text
        canvas.drawText("${animatedProgress.toInt()}%", centerX, centerY + 20f, textPaint)
        if (showSubText) {
            canvas.drawText(subText, centerX, centerY + 60f, subTextPaint)
        }
//        if (showSubText) {
//            canvas.drawText(context.getString(R.string.charging), centerX, centerY + 60f, subTextPaint)
//        }
//
    }
}