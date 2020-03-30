package kr.yangbob.memorization.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.activity_only_first.*
import kr.yangbob.memorization.R
import kr.yangbob.memorization.databinding.ActivityOnlyFirstBinding
import kr.yangbob.memorization.databinding.ActivityOnlyFirstViewpagerBinding

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityOnlyFirstBinding>(this, R.layout.activity_only_first)
        binding.currentIdx = 0
        binding.activity = this
        startViewPager.adapter = OnlyFirstListAdapter()
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

class OnlyFirstListAdapter : RecyclerView.Adapter<OnlyFirstViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnlyFirstViewHolder {
        val binding = ActivityOnlyFirstViewpagerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OnlyFirstViewHolder(binding)
    }

    override fun getItemCount(): Int = 4

    override fun onBindViewHolder(holder: OnlyFirstViewHolder, position: Int) {
        holder.onBind(position)
    }
}


class OnlyFirstViewHolder(private val binding: ActivityOnlyFirstViewpagerBinding) : RecyclerView.ViewHolder(binding.root){
    fun onBind(position: Int){
        binding.index = position
    }
}