package kr.yangbob.memorization.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_entire.*
import kr.yangbob.memorization.EXTRA_TO_QST_ID
import kr.yangbob.memorization.R
import kr.yangbob.memorization.databinding.ItemEntireCardBinding
import kr.yangbob.memorization.db.Qst
import kr.yangbob.memorization.viewmodel.EntireViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class EntireActivity : AppCompatActivity() {
    private val model: EntireViewModel by viewModel()
    private lateinit var qstList: LiveData<List<Qst>>
    private lateinit var adapter: EntireRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entire)

        adapter = EntireRecyclerAdapter(listOf(), model)
        entireRecycler.layoutManager = LinearLayoutManager(this)
        entireRecycler.adapter = adapter

        qstList = model.getAllQst()
        qstList.observe(this, Observer {
            adapter.setData(it)
        })

        toolBar.title = resources.getString(R.string.entire_appbar_title)
        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // up 버튼(HomeAsUp)을 눌렀을 때, Main의 ActivityForResult 가 실행 안되서 따로 작성해준 것
        if(item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}

class EntireViewHolder(private val binding: ItemEntireCardBinding, private val model: EntireViewModel) : RecyclerView.ViewHolder(binding.root){
    fun bind(qst: Qst){
        binding.qst = qst
        binding.holder = this
        binding.tvEntireRegistration.text = model.getFormattedDate(qst.registration_date)
        binding.card.setOnClickListener {
            val context = binding.root.context
            context.startActivity(Intent(context, QstActivity::class.java).putExtra(EXTRA_TO_QST_ID, qst.id))
        }
    }
}
class EntireRecyclerAdapter(private var recordList: List<Qst>, private val model: EntireViewModel) : RecyclerView.Adapter<EntireViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntireViewHolder {
        val binding: ItemEntireCardBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_entire_card, parent, false)
        return EntireViewHolder(binding, model)
    }

    override fun getItemCount(): Int = recordList.size

    override fun onBindViewHolder(holder: EntireViewHolder, position: Int) {
        holder.bind(recordList[position])
    }
    fun setData(recordList: List<Qst>){
        this.recordList = recordList
        notifyDataSetChanged()
    }
}
