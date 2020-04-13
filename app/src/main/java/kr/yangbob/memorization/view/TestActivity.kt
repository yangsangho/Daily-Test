package kr.yangbob.memorization.view

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.ads.AdRequest
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_test.*
import kr.yangbob.memorization.EXTRA_TO_TUTORIAL
import kr.yangbob.memorization.R
import kr.yangbob.memorization.Stage
import kr.yangbob.memorization.adapter.TestPagerAdapter
import kr.yangbob.memorization.db.QstRecord
import kr.yangbob.memorization.todayDate
import kr.yangbob.memorization.viewmodel.TestViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class TestActivity : AppCompatActivity() {
    private val model: TestViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        setContentView(R.layout.activity_test)

        var testRecordList: List<QstRecord>
        model.isDormant = intent.getBooleanExtra("isDormant", false)
        if (model.isDormant) {
            toolBar.title = getString(R.string.test_dormant_appbar_title)
            val partitionList = model.getAllDormantQst().partition { it.cur_stage <= Stage.BEGIN_TWO.ordinal }
            // BEGIN_TWO 이하는 초기화
            partitionList.first.forEach {
                it.is_dormant = false
                it.cur_stage = 0
                it.next_test_date = todayDate
                model.insertQst(it)
                val newQstRecord = QstRecord(it.id!!, todayDate, 1)
                model.insertQstRecord(newQstRecord)
            }
            testRecordList = partitionList.second.map { QstRecord(it.id!!, todayDate, it.cur_stage) }

            Toast.makeText(this, R.string.test_dormant_initialize_msg, Toast.LENGTH_LONG).show()
            if (testRecordList.isEmpty()) finish()
            else {
                val snackBar = Snackbar.make(
                        testLayout,
                        R.string.test_dormant_snackbar_msg,
                        Snackbar.LENGTH_INDEFINITE
                )
                snackBar.setAction(R.string.confirmation) {
                    snackBar.dismiss()
                }
                snackBar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).maxLines = 3
                snackBar.show()
            }
        } else {
            toolBar.title = getString(R.string.test_appbar_title)
            testRecordList = model.getTodayNullRecords()
        }
        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        testRecordList = testRecordList.shuffled()
        viewPager.adapter = TestPagerAdapter(testRecordList, model, viewPager, this)

        val listSize = testRecordList.size
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tvQstCnt.text = String.format("%d/%d", position + 1, listSize)
                qstProgress.max = listSize
                qstProgress.progress = position + 1
            }
        })

        if (model.isFirst()) {
            startActivity(Intent(this, TutorialActivity::class.java).apply {
                putExtra(EXTRA_TO_TUTORIAL, "test")
            })
        }

        adView.loadAd(AdRequest.Builder().build())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        model.resetIsPossibleClick()
        super.onResume()
    }
}