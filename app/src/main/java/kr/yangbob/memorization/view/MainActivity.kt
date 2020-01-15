package kr.yangbob.memorization.view

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.android.synthetic.main.activity_main.*
import kr.yangbob.memorization.R
import kr.yangbob.memorization.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var model: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        model = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            .create(MainViewModel::class.java)

        initUi()
    }

    private fun initUi() {

        val list = ArrayList<BarEntry>()

        val bar1 = BarEntry(1f, 5f)
        val bar2 = BarEntry(2f, 4f)
        val bar3 = BarEntry(3f, 15f)
        val bar4 = BarEntry(4f, 20f)
        val bar5 = BarEntry(5f, 14f)
        val bar6 = BarEntry(6f, 11f)
        val bar7 = BarEntry(7f, 1f)

        list.add(bar1)
        list.add(bar2)
        list.add(bar3)
        list.add(bar4)
        list.add(bar5)
        list.add(bar6)
        list.add(bar7)

        val barDataset1 = BarDataSet(listOf(bar1), "TEST1")
        barDataset1.color = Color.YELLOW
        val barDataset2 = BarDataSet(listOf(bar2), "TEST2")
        barDataset2.color = Color.CYAN
        val barDataset3 = BarDataSet(listOf(bar3), "TEST3")
        barDataset3.color = Color.GREEN
        val barDataset4 = BarDataSet(listOf(bar4), "TEST4")
        barDataset4.color = Color.RED
        val barDataset5 = BarDataSet(listOf(bar5), "TEST5")
        barDataset5.color = Color.DKGRAY
        val barDataset6 = BarDataSet(listOf(bar6), "TEST6")
        barDataset6.color = Color.BLUE
        val barDataset7 = BarDataSet(listOf(bar7), "TEST7")
        barDataset7.color = Color.BLACK
        barDataset7.barBorderColor = Color.BLACK
        barDataset7.setDrawIcons(false) // 안되네


        val barData = BarData(listOf(barDataset1, barDataset2, barDataset3, barDataset4, barDataset5, barDataset6, barDataset7))
        barData.setValueFormatter(MyValueFormatter())
        barData.setValueTypeface(Typeface.DEFAULT_BOLD)
        barData.setValueTextSize(12f)

        todayBarChart.isAutoScaleMinMaxEnabled = true
        todayBarChart.data = barData
        todayBarChart.description.apply {
            text = "개수(비율%)"
            yOffset = -5f
        }
        todayBarChart.xAxis.isEnabled = false
        todayBarChart.axisRight.isEnabled = false
        todayBarChart.axisLeft.isEnabled = false
        todayBarChart.setTouchEnabled(false)


    }

    class MyValueFormatter : ValueFormatter()
    {
        override fun getBarLabel(barEntry: BarEntry?): String {

            val cnt = barEntry?.y?.toInt()
            val ratio = String.format("%.1f", barEntry?.y?.div(70)?.times(100))
            return "$cnt ($ratio%)"
        }

    }

}