package kr.yangbob.memorization.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import kr.yangbob.memorization.R
import kr.yangbob.memorization.databinding.ActivityDetailLayoutListRecordBinding
import kr.yangbob.memorization.db.QstRecord
import kr.yangbob.memorization.viewmodel.QstViewModel

class DetailListAdapter(private val recordList: List<QstRecord>, private val model: QstViewModel) :
        RecyclerView.Adapter<DetailViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val binding: ActivityDetailLayoutListRecordBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.activity_detail_layout_list_record,
                parent,
                false
        )
        return DetailViewHolder(binding, model)
    }

    override fun getItemCount(): Int = recordList.size

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        holder.onBind(recordList[position])
    }
}