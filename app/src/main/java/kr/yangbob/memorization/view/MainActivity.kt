package kr.yangbob.memorization.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_main.*
import kr.yangbob.memorization.R
import kr.yangbob.memorization.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var model: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("TEST", "Main onCreate()")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        model = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            .create(MainViewModel::class.java)

        initUi()

        button.setOnClickListener {
            barChart.setDataList(listOf(1,2,3,4,5,6,7))
        }
    }

    private fun initUi() {
        barChart.setDataList(listOf(1,2,3,4,5,6,7))
    }

}