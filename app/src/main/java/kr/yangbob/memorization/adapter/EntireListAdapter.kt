package kr.yangbob.memorization.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import kr.yangbob.memorization.R
import kr.yangbob.memorization.databinding.ActivityEntireLayoutListQstBinding
import kr.yangbob.memorization.db.Qst
import kr.yangbob.memorization.viewmodel.EntireViewModel

class EntireListAdapter(private var recordList: List<Qst>, private val model: EntireViewModel) : RecyclerView.Adapter<EntireViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntireViewHolder {
        val binding: ActivityEntireLayoutListQstBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.activity_entire_layout_list_qst, parent, false)
        return EntireViewHolder(binding, model)
    }

    override fun getItemCount(): Int = recordList.size

    override fun onBindViewHolder(holder: EntireViewHolder, position: Int) {
        holder.bind(recordList[position])
    }

    fun setData(recordList: List<Qst>) {
        this.recordList = recordList
        notifyDataSetChanged()
    }
}