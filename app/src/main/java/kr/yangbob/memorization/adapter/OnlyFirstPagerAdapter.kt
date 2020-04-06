package kr.yangbob.memorization.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.yangbob.memorization.databinding.ActivityOnlyFirstViewpagerBinding

class OnlyFirstPagerAdapter : RecyclerView.Adapter<OnlyFirstViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnlyFirstViewHolder {
        val binding = ActivityOnlyFirstViewpagerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OnlyFirstViewHolder(binding)
    }

    override fun getItemCount(): Int = 4

    override fun onBindViewHolder(holder: OnlyFirstViewHolder, position: Int) {
        holder.onBind(position)
    }
}