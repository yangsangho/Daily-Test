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
    private lateinit var appBarTitle: String
    private lateinit var copyQstList: List<Qst>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entire)

        adapter = EntireRecyclerAdapter(listOf(), model)
        entireRecycler.layoutManager = LinearLayoutManager(this)
        entireRecycler.adapter = adapter

        qstList = model.getAllQst()
        qstList.observe(this, Observer {
            copyQstList = it
            adapter.setData(it)
            setNoItemMsgVisible(it.isEmpty())
        })

        appBarTitle = getString(R.string.entire_appbar_title)
        toolBar.title = appBarTitle
        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_entire, menu)

        val searchItem = menu?.findItem(R.id.action_entire_search)
        searchItem?.setOnActionExpandListener(object: MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                supportActionBar?.title = ""
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                supportActionBar?.title = appBarTitle
                adapter.setData(copyQstList)
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
                    val newList = copyQstList.filter { it.title.contains(newText, true) }
                    adapter.setData(newList)
                    setNoItemMsgVisible(newList.isEmpty())
                } else {
                    adapter.setData(copyQstList)
                    setNoItemMsgVisible(copyQstList.isEmpty())
                }
                return false
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // up 버튼(HomeAsUp)을 눌렀을 때, Main의 ActivityForResult 가 실행 안되서 따로 작성해준 것
        if(item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setNoItemMsgVisible(isEmpty: Boolean){
        if(isEmpty) entireNoItemMsg.visibility = View.VISIBLE
        else entireNoItemMsg.visibility = View.GONE
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
