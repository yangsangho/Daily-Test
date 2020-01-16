package kr.yangbob.memorization.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.today_question.*
import kotlinx.android.synthetic.main.whole_question.*
import kr.yangbob.memorization.R
import kr.yangbob.memorization.db.Qst
import kr.yangbob.memorization.db.TestIncTitle
import kr.yangbob.memorization.listadapter.TodayQstAdapter
import kr.yangbob.memorization.listadapter.WholeQstAdapter
import kr.yangbob.memorization.viewmodel.MainViewModel

class DashboardFragment(private val model: MainViewModel) : Fragment() {
    private var wholeQstList: List<Qst> = listOf()
    private lateinit var wholeQstAdapter: WholeQstAdapter
    private var todayQstList: List<TestIncTitle> = listOf()
    private lateinit var todayQstAdapter: TodayQstAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("Fragment", "onCreate()")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i("Fragment", "onCreateView()")
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    // UI 설정
    override fun onStart() {
        super.onStart()

        val lm1 = LinearLayoutManager(activity)
        val lm2 = LinearLayoutManager(activity)

        wholeQstAdapter = WholeQstAdapter()
        wholeQstRecycler.adapter = wholeQstAdapter
        wholeQstRecycler.layoutManager = lm1
        wholeQstRecycler.setHasFixedSize(true)

        todayQstAdapter = TodayQstAdapter()
        todayQstRecycler.adapter = todayQstAdapter
        todayQstRecycler.layoutManager = lm2
        todayQstRecycler.setHasFixedSize(true)

        getData()
    }

    private fun getData() {
        wholeQstList = model.getAllQna()

        if (wholeQstList.isNotEmpty()) {
            wholeQstAdapter.setDataSet(wholeQstList)
        } else {
            wholeQstRecycler.visibility = View.INVISIBLE
            wholeQstNoItem.visibility = View.VISIBLE
        }

        todayQstList = model.getAllIncTitle()
        if (todayQstList.isNotEmpty()) {
            todayQstAdapter.setDataSet(todayQstList)
        } else {
            todayQstRecycler.visibility = View.INVISIBLE
            todayQstNoItem.visibility = View.VISIBLE
        }

    }
}
