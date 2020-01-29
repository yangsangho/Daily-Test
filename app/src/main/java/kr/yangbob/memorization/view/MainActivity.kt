package kr.yangbob.memorization.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
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
    private var isInit = false

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
        binding.mainActivity = this

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
                binding.dashboardEntire.dashboardChart.setDataList( map.toSortedMap().values.toList() )
            }
        })

        qstRecordList.observe(this, Observer { rawList ->
            Log.i(logTag, "!!!!!!!! observe qstRecordList : isInit = $isInit !!!!!!!!")
            if(!isInit){
                isInit = true
                model.setTodayTestCount()
                if(rawList.isNotEmpty()){
                    val map = rawList.groupBy { qstRecord -> qstRecord.challenge_stage }.mapValues { it.value.size }.toMutableMap()
                    Stage.values().filter { it.ordinal > 0 }.forEach { if(!map.containsKey(it.ordinal)) map[it.ordinal] = 0 }
                    binding.dashboardToday.dashboardChart.setDataList( map.toSortedMap().values.toList() )
                }
            }
            // 아래 2개 메소드는 시험보고 나서 반복
            model.setTestCompletionRate()
            if( model.setTodayCardData() ) {
                binding.dashboardToday.dashboardBtn1.isEnabled = false
            }
        })
    }

    fun clickAddBtn(view: View){
        startActivity(Intent(this, AddActivity::class.java))
    }
    fun clickTestBtn(view: View){
        startActivity(Intent(this, TestActivity::class.java))
    }
    fun clickEntireList(view: View){

    }

    override fun onDestroy() {
        super.onDestroy()
        isInit = false
    }
}