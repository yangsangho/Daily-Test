package kr.yangbob.memorization.listadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import kr.yangbob.memorization.R
import kr.yangbob.memorization.databinding.ListitemTodayQuestionBinding
import kr.yangbob.memorization.db.TestIncTitle

class TodayQstAdapter : RecyclerView.Adapter<TodayQstAdapter.TodayViewHolder>() {

    private var dataSet: List<TestIncTitle> = listOf()

    override fun getItemCount(): Int = dataSet.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TodayViewHolder {
        val binding = DataBindingUtil.inflate<ListitemTodayQuestionBinding>(
            LayoutInflater.from(parent.context),
            R.layout.list_today_question,
            parent,
            false
        )
        return TodayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodayViewHolder, position: Int) =
        holder.bind(dataSet[position])

    fun setDataSet(dataSet: List<TestIncTitle>) {
        this.dataSet = dataSet
        notifyDataSetChanged()
    }

    inner class TodayViewHolder(private val binding: ListitemTodayQuestionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(test: TestIncTitle) {
            binding.test = test
        }
    }
}