package kr.yangbob.memorization.view

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.*
import kr.yangbob.memorization.R
import kr.yangbob.memorization.databinding.ActivityMainBinding
import kr.yangbob.memorization.db.Qst
import kr.yangbob.memorization.testmanager.TEST_CHK_RECV_ID
import kr.yangbob.memorization.testmanager.TestChkReceiver
import kr.yangbob.memorization.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class MainActivity : AppCompatActivity() {

    private val model: MainViewModel by viewModel()
    private lateinit var qstList: LiveData<List<Qst>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.model = model
        qstList = model.getQstList()
        model.setTodayData()
        model.setTestCompletionRate()

        qstList.observe(this, Observer {
            Log.i("TEST", "observe............!!!!!!!!!!!!!!!")
            model.setEntireCntAverageRegistryCnt()
        })

        setClickEvent()
        setTestChkAlarm()
    }

    private fun setTestChkAlarm(){
        val testChkIntent = Intent(this, TestChkReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, TEST_CHK_RECV_ID, testChkIntent, PendingIntent.FLAG_NO_CREATE)
        if( pendingIntent == null ) {
            Log.i("TEST", "PendingIntent is Null -> Set Alarm!")
            val alarmMgr = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val startTime = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 10)
            }.timeInMillis
            alarmMgr.setRepeating(
                AlarmManager.RTC_WAKEUP,
                startTime,
                AlarmManager.INTERVAL_DAY,
                PendingIntent.getBroadcast(this, TEST_CHK_RECV_ID, testChkIntent, 0)
            )
        } else {
            Log.i("TEST", "PendingIntent is Not Null -> Skip Set Alarm!")
        }
    }
    private fun setClickEvent(){
        addBtn.setOnClickListener{
            startActivity(Intent(this, AddActivity::class.java))
        }
    }
}