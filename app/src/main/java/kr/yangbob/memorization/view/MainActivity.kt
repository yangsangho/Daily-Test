package kr.yangbob.memorization.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.*
import kr.yangbob.memorization.R
import kr.yangbob.memorization.Stage
import kr.yangbob.memorization.databinding.ActivityMainBinding
import kr.yangbob.memorization.db.Qst
import kr.yangbob.memorization.db.QstRecord
import kr.yangbob.memorization.setTestChkAlarm
import kr.yangbob.memorization.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val logTag = "MainActivity"
    private val model: MainViewModel by viewModel()
    private lateinit var qstList: LiveData<List<Qst>>
    private lateinit var qstRecordList: LiveData<List<QstRecord>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        this.deleteDatabase("BeomS_Memo")   // DB 초기화할 때 사용

        // set Alarm
//        cancelAlarm(this)
        setTestChkAlarm(this)

        // binding
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.model = model

        // get List Data
        qstList = model.getQstList()
        qstRecordList = model.getQstRecordList()

        // set BarChart
        binding.dashboardToday.dashboardChart.setCount(7)
        binding.dashboardEntire.dashboardChart.setCount(8)

        // set UI Data
        qstList.observe(this, Observer { rawList ->
            Log.i(logTag, "!!!!!!!! observe qstList !!!!!!!!")
            model.setEntireCardData()
            if(rawList.isNotEmpty()){
                val map = rawList.groupBy { qst -> qst.cur_stage }.mapValues { it.value.size }.toMutableMap()
                Stage.values().forEach { if(!map.containsKey(it.ordinal)) map[it.ordinal] = 0 }
                val list: List<Int> = map.toSortedMap().values.toList()
                binding.dashboardEntire.dashboardChart.setDataList(list)
            }
        })

        model.setTestCompletionRate()
        qstRecordList.observe(this, Observer {
            model.setTodayCardData()
            Log.i(logTag, "!!!!!!!! observe qstRecordList !!!!!!!!")
        })
//        if(qstRecordList.value!!.isNotEmpty()){
//            binding.dashboardToday.dashboardChart.setDataList(listOf(1,2,3,4,5,6,7))
//        }

        setClickEvent()
    }

    private fun setClickEvent(){
        addBtn.setOnClickListener{
            startActivity(Intent(this, AddActivity::class.java))
        }
    }
}