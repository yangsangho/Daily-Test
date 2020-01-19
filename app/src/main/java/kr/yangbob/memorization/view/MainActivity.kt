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
import kr.yangbob.memorization.databinding.ActivityMainBinding
import kr.yangbob.memorization.db.Qst
import kr.yangbob.memorization.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

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

        addBtn.setOnClickListener{
            startActivity(Intent(this, AddActivity::class.java))
        }
    }
}