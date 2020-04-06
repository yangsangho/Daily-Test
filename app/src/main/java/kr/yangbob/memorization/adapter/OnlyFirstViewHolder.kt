package kr.yangbob.memorization.adapter

import androidx.recyclerview.widget.RecyclerView
import kr.yangbob.memorization.databinding.ActivityOnlyFirstViewpagerBinding

class OnlyFirstViewHolder(private val binding: ActivityOnlyFirstViewpagerBinding) : RecyclerView.ViewHolder(binding.root){
    fun onBind(position: Int){
        binding.index = position
    }
}