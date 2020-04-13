package kr.yangbob.memorization.view

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import kr.yangbob.memorization.R
import kr.yangbob.memorization.SETTING_IS_FIRST_MAIN
import kr.yangbob.memorization.adapter.MainFragmentAdapter
import kr.yangbob.memorization.setTimer
import kr.yangbob.memorization.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val model: MainViewModel by viewModel()
    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isMainFirst = model.isFirst(SETTING_IS_FIRST_MAIN)
        if (isMainFirst) startActivity(Intent(this, OnlyFirstActivity::class.java))

        setTimer(this)
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        setContentView(R.layout.activity_main)

        // ToolBar 설정
        toolBar.title = getString(R.string.app_name)
        setSupportActionBar(toolBar)

        model.getDormantQstList().observe(this, Observer {
            if (it.isEmpty()) {
                if (dormantBtn.visibility == View.VISIBLE) {
                    dormantBtn.visibility = View.GONE
                    dormantCnt.visibility = View.GONE
                }
            } else {
                if (dormantBtn.visibility == View.GONE) {
                    dormantBtn.visibility = View.VISIBLE
                    dormantCnt.visibility = View.VISIBLE
                }
                if (it.size > 99) dormantCnt.text = "99+"
                else dormantCnt.text = "${it.size}"
            }
        })

        dormantBtn.setOnClickListener {
            startActivity(Intent(this, TestActivity::class.java).apply {
                putExtra("isDormant", true)
            })
        }

        // Viewpager 및 TabLayout 설정
        mainViewPager.adapter = MainFragmentAdapter(lifecycle, supportFragmentManager)
        mainViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        mainViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tabLayout.getTabAt(position)?.select()
            }
        })
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {}
            override fun onTabUnselected(p0: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab?) {
                mainViewPager.currentItem = tab?.position ?: 0
            }
        })
        if (isMainFirst) mainViewPager.currentItem = 1
    }

    override fun onResume() {
        super.onResume()
        model.resetIsPossibleClick()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_main_write -> {
            if (model.checkIsPossibleClick()) {
                startActivityForResult(Intent(this, CreateActivity::class.java), 2)
            }
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, R.string.main_toast_oneMore_back, Toast.LENGTH_SHORT).show()
        Handler().postDelayed(
                { doubleBackToExitPressedOnce = false },
                2000
        )
    }

    // 전체 문제 쪽에서 버튼 눌러서 activity 갔다가 돌아왔을 때 전체문제 탭이 선택되도록
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        tabLayout.getTabAt(1)?.select()
        mainViewPager.currentItem = 1
    }
}