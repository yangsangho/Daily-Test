package kr.yangbob.memorization.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import kr.yangbob.memorization.R

// Canvas 기본 함수 : drawPoint(), drawLine(), drawRect(), drawCircle(), drawArc(), drawText(), drawBitmap(), drawRoundRect(), drawOval()
// Paint 기본 함수 : setColor(), setARGB, setAntiAlias(),, setStyle(), setStrokeWidth(), setStrokeCap(), setStrokeJoin()
class BarChart : View {
    // 생성자들
    constructor(context: Context) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)

    // 페인트 객체들
    private val textPaint = Paint()

    // 그래프 위치 관련 변수들
    private var centerX: Float = 0f
    private var centerY: Float = 0f
    private var baseLineY: Float = 0f
    private var itemCenterX: Float = 0f
    private var itemWidth: Float = 0f
    private var iconCenterY: Float = 0f
    private var iconHeight: Float = 0f
    private var barMaxHeight: Float = 0f

    // 데이터 세트들
    private var iconRectList: List<RectF> = listOf()
    private var barIconBitmapList: List<Bitmap?> = listOf()
    private var barRectList: List<RectF?> = listOf()
    private var barTextCoordinateList: List<Pair<Float, Float>?> = listOf()
    private var barTextList: List<Pair<String, String>?> = listOf()
    private var dataList: List<Int> = listOf()

    init {
        textPaint.apply {
            color = Color.BLACK
            textAlign = Paint.Align.CENTER
            textSize = resources.getDimensionPixelSize(R.dimen.noItemFontSize)
                .toFloat()    // 리소스 객체를 static 쪽에서 사용 못함
        }

        // Context 객체를 static 쪽에서 사용 못함
        val bitmapList = listOf(
            ContextCompat.getDrawable(context, R.drawable.ic_stage_1_1)?.toBitmap(500, 500),
            ContextCompat.getDrawable(context, R.drawable.ic_stage_1_2)?.toBitmap(500, 500),
            ContextCompat.getDrawable(context, R.drawable.ic_stage_1_3)?.toBitmap(500, 500),
            ContextCompat.getDrawable(context, R.drawable.ic_stage_3)?.toBitmap(500, 500),
            ContextCompat.getDrawable(context, R.drawable.ic_stage_7)?.toBitmap(500, 500),
            ContextCompat.getDrawable(context, R.drawable.ic_stage_15)?.toBitmap(500, 500),
            ContextCompat.getDrawable(context, R.drawable.ic_stage_30)?.toBitmap(500, 500)
        )
        barIconBitmapList = bitmapList
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.i("TEST", "onSizeChanged() width = $width, height = $height")
        centerX = w * 0.5f
        centerY = h * 0.5f

        baseLineY = h * 0.85f
        iconHeight = h * 0.075f
        itemCenterX = w / 14f
        itemWidth = w * 0.05f
        barMaxHeight = h * 0.7f

        iconCenterY = baseLineY + iconHeight
        if (itemWidth > iconHeight) itemWidth = iconHeight
        else iconHeight = itemWidth

        val iconRectArr = MutableList(7) { RectF() }
        for (i in 0..6) {
            val centerX = makeItemCenterX(itemCenterX, i + 1)
            iconRectArr[i].let {
                it.left = centerX - itemWidth
                it.right = centerX + itemWidth
                it.top = iconCenterY - iconHeight
                it.bottom = iconCenterY + iconHeight
            }
        }
        iconRectList = iconRectArr

        makeDesiredTextPaint(itemWidth * 2)

        setDataList(dataList)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        Log.i("TEST", "onDraw()")

        if (dataList.isEmpty()) {
            canvas?.drawText(
                resources.getText(R.string.question_list_no_item_msg).toString(),
                centerX,
                centerY,
                textPaint
            )
        } else {
            canvas?.drawLine(0f, baseLineY, width.toFloat(), baseLineY, blackPaint)
            for (i in 1..6) {
                val x = itemCenterX * 2 * i
                canvas?.drawLine(x, 0f, x, height.toFloat(), grayPaint)
            }

            for ((idx, rectF) in iconRectList.withIndex()) {
                barIconBitmapList[idx]?.let {
                    canvas?.drawBitmap(it, null, rectF, null)
                }
            }

            // Bar 그래프 및 Bar Description 그리기
            for (i in 0..6) {
                barRectList[i]?.let {
                    canvas?.drawRect(it, barColorList[i])
//                    canvas?.drawRect(it, navyPaint)
                    canvas?.drawRect(it, blackBorderPaint)
                }
                if (barTextList[i] != null) {
                    barTextCoordinateList[i]?.let {
                        canvas?.drawText(
                            barTextList[i]!!.first,
                            it.first,
                            it.second - barDescRatioHeight,
                            barDescFontPaint.first
                        )
                        canvas?.drawText(
                            barTextList[i]!!.second,
                            it.first,
                            it.second,
                            barDescFontPaint.second
                        )
                    }
                }
            }
        }
    }

    private fun makeItemCenterX(itemBaseX: Float, position: Int) = itemBaseX * (position * 2 - 1)

    // 외부에서 그래브 데이터 설정하는 함수
    // 그 데이터를 바탕으로, 그래프 Bar 텍스트 및 Bar 박스 좌표, Bar 텍스트 좌표 생성
    fun setDataList(list: List<Int>) {
        Log.i("TEST", "setDataList()")
        if (list.size != 7) throw IllegalArgumentException()

        dataList = list
        val sumData = dataList.sum()
        val maxData = dataList.max()
        val barRectArr = MutableList<RectF?>(7) { null }
        val barTextCoordinateArr = MutableList<Pair<Float, Float>?>(7) { null }
        val barTextArr = MutableList<Pair<String, String>?>(7) { null }

        for ((idx, value) in dataList.withIndex()) {
            if (value <= 0) continue

            val centerX = makeItemCenterX(itemCenterX, idx + 1)
            val barRatio = value / maxData!!.toFloat()
            val barHeight = barMaxHeight * barRatio

            barRectArr[idx] = RectF(
                centerX - itemWidth,
                baseLineY - barHeight,
                centerX + itemWidth,
                baseLineY
            )

            val ratio = String.format("%.1f", (value / sumData.toFloat()) * 100)
            barTextArr[idx] = Pair("$value", "($ratio%)")
            barTextCoordinateArr[idx] = Pair(centerX, baseLineY - (barHeight + barDescRatioHeight))
        }

        barRectList = barRectArr
        barTextList = barTextArr
        barTextCoordinateList = barTextCoordinateArr
        invalidate()
    }

    // Bar Description 폰트 Paint 객체
    private val barDescFontPaint = Pair(
        Paint().apply {
            color = Color.BLACK
            textAlign = Paint.Align.CENTER
        },
        Paint().apply {
            color = Color.BLACK
            textAlign = Paint.Align.CENTER
        })
    // 비율 Description 높이 값
    private var barDescRatioHeight: Int = 0

    // 텍스트와 최대로 채우기 위한 width 값을 넘겨서, 그 width 에 맞춰 최대 FontSize 됨
    // (여기서는 기준 텍스트 받아서 사용)
    private fun makeDesiredTextPaint(desiredWidth: Float) {
        val bounds = Rect()
        val testTextSize = 48f
        var desiredTextSize: Float

        barDescFontPaint.first.textSize = testTextSize
        barDescFontPaint.first.getTextBounds(
            baseTextSizeStr.first,
            0,
            baseTextSizeStr.first.length,
            bounds
        )
        desiredTextSize = testTextSize * desiredWidth / bounds.width()
        barDescFontPaint.first.textSize = desiredTextSize

        barDescFontPaint.second.textSize = testTextSize
        barDescFontPaint.second.getTextBounds(
            baseTextSizeStr.second,
            0,
            baseTextSizeStr.second.length,
            bounds
        )
        desiredTextSize = testTextSize * desiredWidth / bounds.width()
        barDescFontPaint.second.textSize = desiredTextSize

        // 변경된 값으로의 높이를 구해야하니까 한번 더
        barDescFontPaint.second.getTextBounds(
            baseTextSizeStr.second,
            0,
            baseTextSizeStr.second.length,
            bounds
        )
        barDescRatioHeight = bounds.height()        // 비율 Description 높이 값 저장
    }

    companion object {
        // 최대 길이 텍스트의 기준값들
        private val baseTextSizeStr = Pair("99999", "(100.0%)")

        private val barColorList = listOf(
            Paint().apply { color = Color.rgb(0, 0, 0) },
            Paint().apply { color = Color.rgb(85, 0, 0) },
            Paint().apply { color = Color.rgb(170, 0, 0) },
            Paint().apply { color = Color.rgb(255, 0, 0) },
            Paint().apply { color = Color.rgb(255, 85, 0) },
            Paint().apply { color = Color.rgb(255, 170, 0) },
            Paint().apply { color = Color.rgb(255, 255, 0) }
        )

        private val blackBorderPaint = Paint().apply {
            style = Paint.Style.STROKE
            color = Color.BLACK
        }
        private val blackPaint = Paint().apply { color = Color.BLACK }
        private val grayPaint = Paint().apply { color = Color.LTGRAY }
    }
}
