package kr.yangbob.memorization.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import kr.yangbob.memorization.R

class OutlineTextView : AppCompatTextView {
    private var stroke: Boolean = false
    private var strokeWidth: Float = 0f
    private var strokeColor: Int = 0

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle){
        initView(context, attrs)
    }
    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        initView(context, attrs)
    }
    constructor(context: Context): super(context)

    private fun initView(context: Context, attrs: AttributeSet){
        val setAttrs = context.obtainStyledAttributes(attrs, R.styleable.OutlineTextView)
        stroke = setAttrs.getBoolean(R.styleable.OutlineTextView_textStroke, false)
        strokeWidth = setAttrs.getDimension(R.styleable.OutlineTextView_textStrokeWidth, 0f)
        strokeColor = setAttrs.getColor(R.styleable.OutlineTextView_textStrokeColor, ResourcesCompat.getColor(resources, R.color.white, null))
        setAttrs.recycle()
    }

    override fun onDraw(canvas: Canvas?) {
        if(stroke){
            val states: ColorStateList = textColors
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = strokeWidth
            setTextColor(strokeColor)
            super.onDraw(canvas)
            paint.style = Paint.Style.FILL
            setTextColor(states)
        }
        super.onDraw(canvas)
    }
}