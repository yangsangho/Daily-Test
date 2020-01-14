package kr.yangbob.memorization.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import kr.yangbob.memorization.R
import kr.yangbob.memorization.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var dashboardFragment: DashboardFragment
    private lateinit var todaytestFragment: TodaytestFragment
    private lateinit var calendarFragment: CalendarFragment
    private lateinit var model: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        DateFormat.getBestDateTimePattern(Locale.KOREAN, "")

        model = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            .create(MainViewModel::class.java)

        initUi()
        setTebSelectedListener()
    }

    private fun initUi() {
        dashboardFragment = DashboardFragment(model)
        todaytestFragment = TodaytestFragment.newInstance("Test")
        calendarFragment = CalendarFragment.newInstance("Test")

        supportFragmentManager.beginTransaction()
            .replace(R.id.mainFrameLayout, dashboardFragment).commit()
    }

    private fun setTebSelectedListener() {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab?) {
                var fragment: Fragment? = null
                when (tab?.position) {
                    0 -> fragment = dashboardFragment
                    1 -> fragment = todaytestFragment
                    2 -> fragment = calendarFragment
                    else -> {
                    }
                }
                if (fragment != null) supportFragmentManager.beginTransaction()
                    .replace(R.id.mainFrameLayout, fragment).commit()
            }
        })
    }
}
