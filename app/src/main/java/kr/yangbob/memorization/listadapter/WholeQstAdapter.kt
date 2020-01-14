package kr.yangbob.memorization.listadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import kr.yangbob.memorization.R
import kr.yangbob.memorization.databinding.ItemWholeQuestionBinding
import kr.yangbob.memorization.db.Qst

class WholeQstAdapter : RecyclerView.Adapter<WholeQstAdapter.WholeViewHolder>() {

    private var dataSet: List<Qst> = listOf()

    override fun getItemCount(): Int = dataSet.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WholeViewHolder {
        val binding = DataBindingUtil.inflate<ItemWholeQuestionBinding>(
            LayoutInflater.from(parent.context),
            R.layout.whole_question,
            parent,
            false
        )
        return WholeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WholeViewHolder, position: Int) =
        holder.bind(dataSet[position])

    fun setDataSet(dataSet: List<Qst>) {
        this.dataSet = dataSet
        notifyDataSetChanged()
    }

    inner class WholeViewHolder(val binding: ItemWholeQuestionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(qst: Qst) {
            binding.qst = qst
        }
    }
}