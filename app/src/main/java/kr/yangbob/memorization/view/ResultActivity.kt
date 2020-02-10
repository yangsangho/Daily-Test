package kr.yangbob.memorization.view

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_result.*
import kr.yangbob.memorization.EXTRA_TO_QST_ID
import kr.yangbob.memorization.EXTRA_TO_RESULT_DATESTR
import kr.yangbob.memorization.R
import kr.yangbob.memorization.databinding.ItemResultCardBinding
import kr.yangbob.memorization.db.QstRecordWithName
import kr.yangbob.memorization.viewmodel.ResultViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ResultActivity : AppCompatActivity() {
    private val model: ResultViewModel by viewModel()
    private lateinit var recordList: LiveData<List<QstRecordWithName>>
    private lateinit var copyRecordList: List<QstRecordWithName>
    private lateinit var adapter: ResultRecyclerAdapter
    private lateinit var appBarTitle: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        intent.getStringExtra(EXTRA_TO_RESULT_DATESTR)?.let {
            recordList = model.getRecordList(it)
            tvDate.text = model.getFormattedDate(it)
        }

        recordList.observe(this, Observer { rawList ->
            val cntQst = rawList.size
            val cntSolved = rawList.count { it.is_correct != null }
            val cntCorrect = rawList.count { it.is_correct == true }

            val progressRate = if (cntQst > 0) cntSolved / cntQst.toFloat() * 100
            else 0f
            val correctRate = if (cntSolved > 0) cntCorrect / cntSolved.toFloat() * 100
            else 0f

            tvInfo.text = if (cntQst <= 0) resources.getString(R.string.status_msg_no_test)
            else String.format(
                resources.getString(R.string.result_info_format),
                cntQst,
                progressRate,
                correctRate
            )
            copyRecordList = rawList
            adapter.setData(rawList)
            setNoItemMsgVisible(cntQst == 0)
        })

        adapter = ResultRecyclerAdapter(listOf(), model)
        resultRecycler.layoutManager = LinearLayoutManager(this)
        resultRecycler.adapter = adapter

        appBarTitle = resources.getString(R.string.result_appbar_title)
        toolBar.title = appBarTitle
        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onResume() {
        super.onResume()
        model.resetIsPossibleClick()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_result, menu)

        val searchItem = menu?.findItem(R.id.action_result_search)
        searchItem?.setOnActionExpandListener(object: MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                supportActionBar?.title = ""
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                supportActionBar?.title = appBarTitle
                adapter.setData(copyRecordList)
                return true
            }
        })

        val searchView = searchItem?.actionView as SearchView
        val searchAutoComplete = searchView.findViewById<SearchView.SearchAutoComplete>(androidx.appcompat.R.id.search_src_text)
        searchAutoComplete.setHintTextColor(ContextCompat.getColor(this, R.color.white))
        searchView.queryHint = getString(R.string.entire_search_msg)
        searchView.maxWidth = Integer.MAX_VALUE
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText != null){
                    val newList = copyRecordList.filter { it.qst_name.contains(newText, true) }
                    adapter.setData(newList)
                    setNoItemMsgVisible(newList.isEmpty())
                } else {
                    adapter.setData(copyRecordList)
                    setNoItemMsgVisible(copyRecordList.isEmpty())
                }
                return false
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            if(model.checkIsPossibleClick()){
                finish()
            }
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
    private fun setNoItemMsgVisible(isEmpty: Boolean){
        if(isEmpty) resultNoItemMsg.visibility = View.VISIBLE
        else resultNoItemMsg.visibility = View.GONE
    }
}

class ResultViewHolder(private val binding: ItemResultCardBinding, private val model: ResultViewModel) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(record: QstRecordWithName) {
        binding.recordWithName = record
        binding.card.setOnClickListener {
            if(model.checkIsPossibleClick()){
                val context = binding.root.context
                context.startActivity(
                    Intent(context, QstActivity::class.java).putExtra(
                        EXTRA_TO_QST_ID,
                        record.qst_id
                    )
                )
            }
        }
    }
}

class ResultRecyclerAdapter(private var recordList: List<QstRecordWithName>, private val model: ResultViewModel) :
    RecyclerView.Adapter<ResultViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val binding: ItemResultCardBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_result_card,
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