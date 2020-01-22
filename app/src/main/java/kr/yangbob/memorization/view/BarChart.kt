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
    private val noItemMsgPaint = Paint().apply {
        color = Color.BLACK
        textAlign = Paint.Align.CENTER
        textSize = resources.getDimensionPixelSize(R.dimen.noItemFontSize).toFloat()
        isAntiAlias = true
    }
    private lateinit var barCntDescFontPaint: Paint
    private lateinit var barRatioDescFontPaint: Paint

    // 그래프 위치 관련 변수들
    private var centerX: Float = 0f
    private var centerY: Float = 0f
    private var baseLineY: Float = 0f
    private var itemCenterX: Float = 0f
    private var itemWidth: Float = 0f
    private var itemIconCenterY: Float = 0f
    private var itemIconHeight: Float = 0f
    private var itemBarMaxHeight: Float = 0f
    private var barDescRatioHeight: Int =
        0             // 비율 Description 높이 값 (비율 text 위에 개수 text 들어가도록)

    // 데이터 세트들
    private var dataList: List<Int> = listOf()
    private lateinit var barRectList: List<RectF?>
    private lateinit var barCntDescTextList: List<String?>
    private lateinit var barRatioDescTextList: List<String?>
    private lateinit var iconRectList: List<RectF>
    private val iconBitmapList: List<Bitmap?> = listOf(
        ContextCompat.getDrawable(context, R.drawable.ic_stage_1_1)?.toBitmap(500, 500),
        ContextCompat.getDrawable(context, R.drawable.ic_stage_1_2)?.toBitmap(500, 500),
        ContextCompat.getDrawable(context, R.drawable.ic_stage_1_3)?.toBitmap(500, 500),
        ContextCompat.getDrawable(context, R.drawable.ic_stage_3)?.toBitmap(500, 500),
        ContextCompat.getDrawable(context, R.drawable.ic_stage_7)?.toBitmap(500, 500),
        ContextCompat.getDrawable(context, R.drawable.ic_stage_15)?.toBitmap(500, 500),
        ContextCompat.getDrawable(context, R.drawable.ic_stage_30)?.toBitmap(500, 500)
    )

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.i("yangtest", "onSizeChanged() width = $width, height = $height")

        // display 사이즈에 따른 각 항목 위치 및 길이 설정
        centerX = w * 0.5f
        centerY = h * 0.5f

        baseLineY = h * 0.85f
        itemIconHeight = h * 0.075f
        itemCenterX = w / 14f
        itemWidth = w * 0.05f
        itemBarMaxHeight = h * 0.7f

        itemIconCenterY = baseLineY + itemIconHeight
        if (itemWidth > itemIconHeight) itemWidth = itemIconHeight
        else itemIconHeight = itemWidth

        // Bar Description 최대 Font Size 구하는 함수
        barCntDescFontPaint =
            makeDesiredTextPaint(itemWidth * 2, baseTextCntDescription, noItemMsgPaint)
        barRatioDescFontPaint =
            makeDesiredTextPaint(itemWidth * 2, baseTextRatioDescription, noItemMsgPaint)
        // Ratio Bar Description 높이 구하기
        barDescRatioHeight = calcBarDescRatioHeight(barRatioDescFontPaint)

        iconRectList = makeIconRect()
        barRectList = makeBarRect(dataList)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        Log.i("yangtest", "onDraw()")

        if (dataList.isNullOrEmpty()) {
            canvas?.drawText(
                resources.getText(R.string.no_item_msg).toString(),
                centerX,
                centerY,
                noItemMsgPaint
            )
        } else {
            // 가로줄 그리기
            canvas?.drawLine(0f, baseLineY, width.toFloat(), baseLineY, blackPaint)

            for (idx in 0 until numberOfItems) {
                val position = idx + 1

                // 세로줄 그리기
                val verticalLineX = makeVerticalLineX(itemCenterX, position)
                canvas?.drawLine(
                    verticalLineX,
                    0f,
                    verticalLineX,
                    height.toFloat(),
                    grayPaint
                )  // 세로줄 그리기

                // Bar 밑에 ICON 그리기
                iconBitmapList[idx]?.let {
                    canvas?.drawBitmap(it, null, iconRectList[idx], bitmapPaint)
                }

                // Bar 그리기
                barRectList[idx]?.let { rectF ->
                    canvas?.drawRect(rectF, barColorList[idx])     // 채워진 박스 그리기
                    canvas?.drawRect(rectF, blackBorderPaint)      // border 박스 그리기

                    // Bar Description 그리기
                    var descY = rectF.top - barDescRatioHeight
                    val descX = makeItemCenterX(itemCenterX, position)
                    barRatioDescTextList[idx]?.let {
                        canvas?.drawText(
                            it,
                            descX,
                            descY,
                            barRatioDescFontPaint
                        )
                    }
                    descY -= barDescRatioHeight
                    barCntDescTextList[idx]?.let {
                        canvas?.drawText(
                            it,
                            descX,
                            descY,
                            barCntDescFontPaint
                        )
                    }
                }
            }
        }
    }

    fun setDataList(list: List<Int>) {
        Log.i("yangtest", "setDataList()")
        if (list.isEmpty()) throw IllegalArgumentException()
        if (list.size != numberOfItems) throw IllegalArgumentException()

        dataList = list

        val sumData = list.sum()
        barCntDescTextList = list.map { if (it > 0) "$it" else null }
        barRatioDescTextList = list.map {
            if (it > 0) "(${String.format(
                "%.1f",
                (it / sumData.toFloat()) * 100
            )}%)" else null
        }
        barRectList = makeBarRect(list)

        invalidate()
    }

    private fun makeBarRect(list: List<Int>): List<RectF?> {
        if (list.isEmpty()) return listOf()

        val maxData = dataList.max()!!
        return list.mapIndexed { idx, data ->
            if (data <= 0) null
            else {
                val centerX = makeItemCenterX(itemCenterX, idx + 1)
                val barRatio = data / maxData.toFloat()
                val barHeight = itemBarMaxHeight * barRatio
                RectF(centerX - itemWidth, baseLineY - barHeight, centerX + itemWidth, baseLineY)
            }
        }
    }

    private tailrec fun makeIconRect(position: Int = 1, acc: List<RectF> = listOf()): List<RectF> =
        when {
            position > numberOfItems -> acc
            else -> {
                val centerX = makeItemCenterX(itemCenterX, position)
                val rect = RectF(
                    centerX - itemWidth,
                    itemIconCenterY - itemIconHeight,
                    centerX + itemWidth,
                    itemIconCenterY + itemIconHeight
                )
                makeIconRect(position + 1, acc + listOf(rect))
            }
        }

    private fun makeItemCenterX(itemBaseX: Float, position: Int) = itemBaseX * (position * 2 - 1)
    private fun makeVerticalLineX(itemBaseX: Float, position: Int) = itemBaseX * position * 2

    private fun makeDesiredTextPaint(
        desiredWidth: Float,
        baseText: String,
        basePaint: Paint
    ): Paint {
        val paint = Paint(basePaint)
        val bounds = Rect()
        val desiredTextSize: Float

        paint.getTextBounds(baseText, 0, baseText.length, bounds)
        desiredTextSize = paint.textSize * desiredWidth / bounds.width()
        paint.textSize = desiredTextSize

        return paint
    }

    private fun calcBarDescRatioHeight(paint: Paint): Int {
        val bounds = Rect()
        paint.getTextBounds(baseTextRatioDescription, 0, baseTextRatioDescription.length, bounds)
        return bounds.height()
    }

    companion object {
        // 최대 길이의 텍스트 - 최적의 Description Font Size 를 구하기 위한 기준값들
        private const val baseTextCntDescription = "99999"
        private const val baseTextRatioDescription = "(100.0%)"

        private const val numberOfItems = 7

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
        private val bitmapPaint = Paint(Paint.FILTER_BITMAP_FLAG)
    }
}
