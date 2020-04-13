package kr.yangbob.memorization.adapter

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import kr.yangbob.memorization.*
import kr.yangbob.memorization.databinding.ActivityMainLayoutDashboardBinding
import kr.yangbob.memorization.view.*
import kr.yangbob.memorization.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class MainFragment : Fragment() {
    private val model: MainViewModel by sharedViewModel()
    private lateinit var binding: ActivityMainLayoutDashboardBinding
    private var testRecordCnt = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.activity_main_layout_dashboard, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let { bundle ->
            val isToday = bundle.getBoolean("isToday")
            binding.isToday = isToday
            binding.model = model
            binding.fragment = this
            binding.isNoItemViewActivate = false
            binding.isBtn1Activate = true
            binding.isBtn2Activate = true
            if (isToday) {
                binding.dashboardChart.setCount(7)
                val observeList = model.getQstRecordList()
                observeList.observe(viewLifecycleOwner, Observer { rawList ->
                    model.setTodayTestCount()
                    if (testRecordCnt != rawList.size) {
                        testRecordCnt = rawList.size
                        if (rawList.isNotEmpty()) {
                            if (model.isFirst(SETTING_IS_FIRST_TODAY)) startTutorial(isToday = true)

                            val map = rawList.groupBy { qstRecord -> qstRecord.challenge_stage }.mapValues { it.value.size }.toMutableMap()
                            STAGE_LIST.filter { it.ordinal > 0 }.forEach { if (!map.containsKey(it.ordinal)) map[it.ordinal] = 0 }
                            val reviewCnt = map[Stage.REVIEW.ordinal]
                            map.remove(Stage.REVIEW.ordinal)
                            map[Stage.AFTER_MONTH.ordinal] = (map[Stage.AFTER_MONTH.ordinal]
                                    ?: 0) + (reviewCnt ?: 0)
                            binding.isNoItemViewActivate = false
                            binding.isHelpIconActivate = true
                            binding.isBtn2Activate = true  // 이건 없어도 될 것 같은데 말야
                            binding.dashboardChart.setDataList(map.toSortedMap().values.toList())
                        } else {
                            binding.isHelpIconActivate = false
                            binding.isNoItemViewActivate = true
                            binding.isBtn2Activate = false
                            binding.dashboardChart.setDataList(listOf())
                        }
                    }
                    binding.isBtn1Activate = !model.setTodayCardData()
                })
            } else {
                binding.dashboardChart.setCount(8)
                val observeList = model.getQstList()
                observeList.observe(viewLifecycleOwner, Observer { rawList ->
                    model.setEntireCardData()
                    if (rawList.isNotEmpty()) {
                        if (model.isFirst(SETTING_IS_FIRST_ENTIRE)) startTutorial(isToday = false)

                        val map = rawList.groupBy { qst -> qst.cur_stage }.mapValues { it.value.size }.toMutableMap()
                        STAGE_LIST.filter { it.ordinal < 8 }.forEach { if (!map.containsKey(it.ordinal)) map[it.ordinal] = 0 }
                        binding.isNoItemViewActivate = false
                        binding.dashboardChart.setDataList(map.toSortedMap().values.toList())
                        binding.isBtn1Activate = true
                        binding.isHelpIconActivate = true
                    } else {
                        binding.isHelpIconActivate = false
                        binding.isNoItemViewActivate = true
                        binding.dashboardChart.setDataList(listOf())
                        binding.isBtn1Activate = false
                    }
                    model.setTestCompletionRate()
                })
            }
        }
    }

    companion object {
        fun newInstance(isToday: Boolean) = MainFragment().apply {
            arguments = Bundle().apply {
                putBoolean("isToday", isToday)
            }
        }
    }

    fun startTutorial(isToday: Boolean) {
        startActivity(Intent(context, TutorialActivity::class.java).apply {
            putExtra(EXTRA_TO_TUTORIAL, if (isToday) "today" else "entire")
        })
    }

    fun clickTestBtn(view: View) {
        if (model.checkIsPossibleClick()) {
            startActivity(Intent(context, TestActivity::class.java))
        }
    }

    fun clickEntireList(view: View) {
        if (model.checkIsPossibleClick()) {
            startActivityForResult(
                    Intent(context, EntireActivity::class.java),
                    0
            )  // main으로 복귀할 때 2번쩨 page로 가도록
        }
    }

    fun clickTodayRecord(view: View) {
        if (model.checkIsPossibleClick()) {
            startActivity(Intent(context, ResultActivity::class.java).apply {
                putExtra(EXTRA_TO_RESULT_DATESTR, todayDate.getDateInt())
            })
        }
    }

    fun clickEntireRecord(view: View) {
        if (model.checkIsPossibleClick()) {
            startActivityForResult(
                    Intent(context, CalendarActivity::class.java),
                    1
            ) // main으로 복귀할 때 2번쩨 page로 가도록
        }
    }
}