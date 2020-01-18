package kr.yangbob.memorization.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import kr.yangbob.memorization.R
import kr.yangbob.memorization.databinding.ActivityMainBinding
import kr.yangbob.memorization.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    val model: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.model = model
//        val todayChart = binding.dashboardToday.dashboardChart
//        val entireChart = binding.dashboardEntire.dashboardChart
//        todayChart.setDataList(listOf(1,2,3,4,5,6,7))
//        entireChart.setDataList(listOf(7,6,5,4,3,2,1))

        initUi()
    }

    private fun initUi() {

    }

}