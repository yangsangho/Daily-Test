package kr.yangbob.memorization.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_result.*
import kr.yangbob.memorization.EXTRA_TO_RESULT_DATESTR
import kr.yangbob.memorization.R
import kr.yangbob.memorization.databinding.ItemResultCardBinding
import kr.yangbob.memorization.db.QstRecordWithName
import kr.yangbob.memorization.viewmodel.ResultViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ResultActivity : AppCompatActivity() {
    private val model: ResultViewModel by viewModel()
    private lateinit var recordList: LiveData<List<QstRecordWithName>>
    private lateinit var adapter: ResultRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        intent.getStringExtra(EXTRA_TO_RESULT_DATESTR)?.let{
            recordList = model.getRecordList(it)
            tvDate.text = model.getFormattedDate(it)
        }

        recordList.observe(this, Observer {rawList ->
            val cntQst = rawList.size
            val cntSolved = rawList.count { it.is_correct != null }
            val cntCorrect = rawList.count { it.is_correct == true }

            val progressRate = if(cntQst > 0) cntSolved / cntQst.toFloat() * 100
            else 0f
            val correctRate = if(cntSolved > 0) cntCorrect / cntSolved.toFloat() * 100
            else 0f

            tvInfo.text = String.format( resources.getString(R.string.result_info_format), cntQst, progressRate, correctRate)

            adapter.setData(rawList)
        })

        adapter = ResultRecyclerAdapter(listOf())
        resultRecycler.layoutManager = LinearLayoutManager(this)
        resultRecycler.adapter = adapter

        toolBar.title = resources.getString(R.string.result_appbar_title)
        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}

class ResultViewHolder(private val binding: ItemResultCardBinding) : RecyclerView.ViewHolder(binding.root){
    fun bind(record: QstRecordWithName){
        binding.recordWithName = record
    }
}
class ResultRecyclerAdapter(private var recordList: List<QstRecordWithName>) : RecyclerView.Adapter<ResultViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val binding: ItemResultCardBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_result_card, parent, false)
        return ResultViewHolder(binding)
    }

    override fun getItemCount(): Int = recordList.size

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        holder.bind(recordList[position])
    }
    fun setData(recordList: List<QstRecordWithName>){
        this.recordList = recordList
        notifyDataSetChanged()
    }
}