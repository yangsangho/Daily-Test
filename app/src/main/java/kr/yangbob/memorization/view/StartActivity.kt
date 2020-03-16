package kr.yangbob.memorization.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.activity_start.*
import kr.yangbob.memorization.R
import kr.yangbob.memorization.databinding.ActivityStartBinding
import kr.yangbob.memorization.databinding.PagerStartActivityBinding

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityStartBinding>(this, R.layout.activity_start)
        binding.currentIdx = 0
        binding.activity = this
        startViewPager.adapter = StartPagerAdapter()
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
        if(index == 3){
            finish()
        } else {
            startViewPager.currentItem = index + 1
        }
    }
}

class StartPagerAdapter : RecyclerView.Adapter<StartViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StartViewHolder {
        val binding = PagerStartActivityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StartViewHolder(binding)
    }

    override fun getItemCount(): Int = 4

    override fun onBindViewHolder(holder: StartViewHolder, position: Int) {
        holder.onBind(position)
    }
}


class StartViewHolder(private val binding: PagerStartActivityBinding) : RecyclerView.ViewHolder(binding.root){
    fun onBind(position: Int){
        binding.index = position
    }
}