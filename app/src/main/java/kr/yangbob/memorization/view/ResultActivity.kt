package kr.yangbob.memorization.view

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.activity_result.*
import kr.yangbob.memorization.EXTRA_TO_RESULT_DATESTR
import kr.yangbob.memorization.R
import kr.yangbob.memorization.adapter.ResultListAdapter
import kr.yangbob.memorization.data.SimpleDate
import kr.yangbob.memorization.db.QstRecordWithName
import kr.yangbob.memorization.viewmodel.ResultViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.DateFormat

class ResultActivity : AppCompatActivity() {
    private val model: ResultViewModel by viewModel()
    private lateinit var recordList: LiveData<List<QstRecordWithName>>
    private lateinit var sortedRecordList: List<QstRecordWithName>
    private lateinit var adapter: ResultListAdapter
    private lateinit var appBarTitle: String
    private lateinit var sortDialog: SortDialog
    private val deleteSet = HashSet<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        setContentView(R.layout.activity_result)

        intent.getIntExtra(EXTRA_TO_RESULT_DATESTR, 0).apply {
            if (this == 0) throw IllegalArgumentException()
//            val date = SimpleDate(this)
            val date = SimpleDate.newInstanceFromDateInt(this)
            recordList = model.getRecordList(date)
            tvDate.text = date.getFormattedDate(DateFormat.FULL)
        }

        recordList.observe(this, Observer { rawList ->
            val cntQst = rawList.size
            val cntSolved = rawList.count { it.is_correct != null }
            val cntCorrect = rawList.count { it.is_correct == true }

            val progressRate = if (cntQst > 0) cntSolved / cntQst.toFloat() * 100
            else 0f
            val correctRate = if (cntSolved > 0) cntCorrect / cntSolved.toFloat() * 100
            else 0f

            tvInfo.text = if (cntQst <= 0) getString(R.string.status_msg_no_test)
            else String.format(
                    getString(R.string.result_info_format),
                    cntQst,
                    progressRate,
                    correctRate
            )
            sortedRecordList = model.getSortedList(rawList)
            adapter.setData(sortedRecordList)
            setNoItemMsgVisible(cntQst == 0)
        })

        adapter = ResultListAdapter(listOf(), model)
        resultRecycler.layoutManager = LinearLayoutManager(this)
        resultRecycler.adapter = adapter

        appBarTitle = getString(R.string.result_appbar_title)
        toolBar.title = appBarTitle
        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        sortDialog = SortDialog(this, model.getSortInfo(), listOf(getString(R.string.result_alert_sort1), getString(R.string.result_alert_sort2), getString(R.string.result_alert_sort3)))
        sortDialog.getChangeObserver().observe(this, Observer {
            model.saveSortInfo(it)
            sortedRecordList = model.getSortedList(sortedRecordList)
            adapter.setData(sortedRecordList)
        })

        adView.loadAd(AdRequest.Builder().build())
    }

    override fun onResume() {
        super.onResume()
        model.resetIsPossibleClick()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val deleteList = data?.getIntegerArrayListExtra("deleteList")
            deleteList?.also {
                deleteSet.addAll(it)
            }
            setResult(RESULT_OK, Intent().apply {
                putExtra("deleteSet", deleteSet)
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.entire_and_result, menu)

        val searchItem = menu?.findItem(R.id.action_entire_search)
        searchItem?.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                supportActionBar?.title = ""
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                supportActionBar?.title = appBarTitle
                adapter.setData(sortedRecordList)
                return true
            }
        })

        val searchView = searchItem?.actionView as SearchView
        searchView.queryHint = getString(R.string.entire_search_msg)
        searchView.maxWidth = Integer.MAX_VALUE
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    val newList = sortedRecordList.filter { it.qst_name.contains(newText, true) }
                    adapter.setData(newList)
                    setNoItemMsgVisible(newList.isEmpty())
                } else {
                    adapter.setData(sortedRecordList)
                    setNoItemMsgVisible(sortedRecordList.isEmpty())
                }
                return false
            }
        })
        val searchAutoComplete = searchView.findViewById<SearchView.SearchAutoComplete>(androidx.appcompat.R.id.search_src_text)
        searchAutoComplete.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            if (model.checkIsPossibleClick()) {
                finish()
            }
            true
        }
        R.id.action_entire_sort -> {
            if (sortedRecordList.size <= 1) {
                Toast.makeText(this, R.string.nothing_sort_msg, Toast.LENGTH_SHORT).show()
            } else {
                if (model.checkIsPossibleClick()) {
                    sortDialog.show()
                    Handler().postDelayed({
                        model.resetIsPossibleClick()
                    }, 1000)
                }
            }
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun setNoItemMsgVisible(isEmpty: Boolean) {
        if (isEmpty) resultNoItemMsg.visibility = View.VISIBLE
        else resultNoItemMsg.visibility = View.GONE
    }
}