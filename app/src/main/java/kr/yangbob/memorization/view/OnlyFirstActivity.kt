package kr.yangbob.memorization.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.activity_only_first.*
import kr.yangbob.memorization.R
import kr.yangbob.memorization.adapter.OnlyFirstPagerAdapter
import kr.yangbob.memorization.databinding.ActivityOnlyFirstBinding

class OnlyFirstActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityOnlyFirstBinding>(this, R.layout.activity_only_first)
        binding.currentIdx = 0
        binding.activity = this

        startViewPager.adapter = OnlyFirstPagerAdapter()
        startViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.currentIdx = position
            }
        })
    }

    fun clickSkip(){
        finish()
    }

    fun clickNextOrStart(index: Int){
        if(index == 3)
            finish()
        else
            startViewPager.currentItem = index + 1
    }
}
