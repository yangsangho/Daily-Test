package kr.yangbob.memorization.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import kr.yangbob.memorization.R
import kr.yangbob.memorization.databinding.ActivityResultLayoutListRecordBinding
import kr.yangbob.memorization.data.QstRecordWithName
import kr.yangbob.memorization.viewmodel.ResultViewModel

class ResultListAdapter(private var recordList: List<QstRecordWithName>, private val model: ResultViewModel) :
        RecyclerView.Adapter<ResultViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val binding: ActivityResultLayoutListRecordBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.activity_result_layout_list_record,
                parent,
                false
        )
        return ResultViewHolder(binding, model)
    }

    override fun getItemCount(): Int = recordList.size

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        holder.bind(recordList[position])
    }

    fun setData(recordList: List<QstRecordWithName>) {
        this.recordList = recordList
        notifyDataSetChanged()
    }
}