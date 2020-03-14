package kr.yangbob.memorization.view

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import kr.yangbob.memorization.R


// Canvas 기본 함수 : drawPoint(), drawLine(), drawRect(), drawCircle(), drawArc(), drawText(), drawBitmap(), drawRoundRect(), drawOval()
// Paint 기본 함수 : setColor(), setARGB, setAntiAlias(),, setStyle(), setStrokeWidth(), setStrokeCap(), setStrokeJoin()
class BarChart : View {
    // 생성자들
    constructor(context: Context) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)

    fun setCount(count: Int){
        numberOfItems = count
        if(count == 7) {
            additionalIconIdx = 1
            strDesc = context.resources.getString(R.string.chart_desc_today)
        } else {
            strDesc = context.resources.getString(R.string.chart_desc_entire)
        }
    }
    private var numberOfItems: Int = 0
    private var additionalIconIdx: Int = 0

    // 페인트 객체들
    private val noItemMsgPaint = TextPaint().apply {
        color = Color.BLACK
        textAlign = Paint.Align.CENTER
        textSize = resources.getDimensionPixelSize(R.dimen.noItemFontSize).toFloat()
        isAntiAlias = true
//        typeface = ResourcesCompat.getFont(context, R.font.font_nanum)
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
    private var descY: Float = 0f
    private var descHeight: Float = 0f
    private var descWidth: Float = 0f
    private var barDescRatioHeight: Int = 0 // 비율 Description 높이 값 (비율 text 위에 개수 text 들어가도록)
    private val maximumTextSize: Float = resources.getDimensionPixelSize(R.dimen.size_list_title).toFloat()

    // 데이터 세트들
    private var dataList: List<Int> = listOf()
    private lateinit var barRectList: List<RectF?>
    private lateinit var barCntDescTextList: List<String?>
    private lateinit var barRatioDescTextList: List<String?>
    private lateinit var iconRectList: List<Rect>
    private val iconVectorList: List<VectorDrawableCompat?> = listOf(
        VectorDrawableCompat.create(context.resources, R.drawable.ic_stage_new, null),
        VectorDrawableCompat.create(context.resources, R.drawable.ic_stage_1_1, null),
        VectorDrawableCompat.create(context.resources, R.drawable.ic_stage_1_2, null),
        VectorDrawableCompat.create(context.resources, R.drawable.ic_stage_1_3, null),
        VectorDrawableCompat.create(context.resources, R.drawable.ic_stage_3, null),
        VectorDrawableCompat.create(context.resources, R.drawable.ic_stage_7, null),
        VectorDrawableCompat.create(context.resources, R.drawable.ic_stage_15, null),
        VectorDrawableCompat.create(context.resources, R.drawable.ic_stage_30, null)
    )
    private lateinit var strDesc: String

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // display 사이즈에 따른 각 항목 위치 및 길이 설정
        centerX = w * 0.5f
        centerY = h * 0.5f
        descY = h * 0.1f
        descHeight = h * 0.08f
        descWidth = w * 0.7f
        baseLineY = h * 0.85f
        itemIconHeight = h * 0.0725f
        itemCenterX = w / (numberOfItems * 2).toFloat()
        itemWidth = w * 0.05f
        itemBarMaxHeight = h * 0.6f

        itemIconCenterY = baseLineY + itemIconHeight + (h*0.005f)
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
        if (dataList.isNotEmpty()) {
            // 가로줄 그리기
            canvas?.drawLine(0f, baseLineY, width.toFloat(), baseLineY, blackPaint)

            // 그래프 설명 글
           canvas?.drawText(strDesc, centerX, descY, makeDesiredDescPaint(descWidth, descHeight, strDesc, noItemMsgPaint))

            for (idx in 0 until numberOfItems) {
                val position = idx + 1

                // Bar 밑에 ICON 그리기
                iconVectorList[idx + additionalIconIdx]?.bounds = iconRectList[idx]
                iconVectorList[idx + additionalIconIdx]?.draw(canvas!!)

                // Bar 그리기
                barRectList[idx]?.let { rectF ->
                    canvas?.drawRect(rectF, barColorList)     // 채워진 박스 그리기
//                    canvas?.drawRect(rectF, blackBorderPaint)      // border 박스 그리기

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
        dataList = list
        if (list.isNotEmpty()){
            if (list.size != numberOfItems) throw IllegalArgumentException()

            val sumData = list.sum()
            barCntDescTextList = list.map { if (it > 0) "$it" else null }
            barRatioDescTextList = list.map {
                if (it > 0) "(${String.format(
                    "%.1f",
                    (it / sumData.toFloat()) * 100
                )}%)" else null
            }
            barRectList = makeBarRect(list)
        }
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

    private tailrec fun makeIconRect(position: Int = 1, acc: List<Rect> = listOf()): List<Rect> =
        when {
            position > numberOfItems -> acc
            else -> {
                val centerX = makeItemCenterX(itemCenterX, position)
                val rect = Rect(
                    (centerX - itemWidth).toInt(),
                    (itemIconCenterY - itemIconHeight).toInt(),
                    (centerX + itemWidth).toInt(),
                    (itemIconCenterY + itemIconHeight).toInt()
                )
                makeIconRect(position + 1, acc + listOf(rect))
            }
        }

    private fun makeItemCenterX(itemBaseX: Float, position: Int) = itemBaseX * (position * 2 - 1)
//    private fun makeVerticalLineX(itemBaseX: Float, position: Int) = itemBaseX * position * 2

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

        if(paint.textSize > maximumTextSize) paint.textSize = maximumTextSize

        return paint
    }

    private fun makeDesiredDescPaint(
        desiredWidth: Float,
        desiredHeight: Float,
        baseText: String,
        basePaint: Paint
    ): Paint {
        val paint = Paint(basePaint)
        val bounds = Rect()
        var desiredTextSize: Float

        paint.getTextBounds(baseText, 0, baseText.length, bounds)
        desiredTextSize = paint.textSize * desiredHeight / bounds.height()
        paint.textSize = desiredTextSize

        paint.getTextBounds(baseText, 0, baseText.length, bounds)
        if(bounds.width() > desiredWidth){
            desiredTextSize = paint.textSize * desiredWidth / bounds.width()
            paint.textSize = desiredTextSize
        }

        if(paint.textSize > maximumTextSize) paint.textSize = maximumTextSize

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
//        private const val logTag = "BarChart"

        private val barColorList = Paint().apply { color = Color.rgb(254, 199, 121) }

//        private val blackBorderPaint = Paint().apply {
//            style = Paint.Style.STROKE
//            color = Color.BLACK
//        }
        private val blackPaint = Paint().apply { color = Color.BLACK }
//        private val grayPaint = Paint().apply { color = Color.LTGRAY }
    }
}
