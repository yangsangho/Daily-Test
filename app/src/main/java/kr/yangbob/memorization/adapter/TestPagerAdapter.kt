package kr.yangbob.memorization.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kr.yangbob.memorization.R
import kr.yangbob.memorization.databinding.ActivityTestViewpagerBinding
import kr.yangbob.memorization.db.QstRecord
import kr.yangbob.memorization.viewmodel.TestViewModel

class TestPagerAdapter(private val testList: List<QstRecord>, private val model: TestViewModel, private val pager: ViewPager2, private val activity: Activity) : RecyclerView.Adapter<TestViewHolder>() {
    override fun getItemCount(): Int = testList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        val binding: ActivityTestViewpagerBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.activity_test_viewpager,
                parent,
                false
        )
        return TestViewHolder(model, binding, this)
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        holder.onBind(testList[position])
    }

    fun move(position: Int) {
        if (!testList.any { it.is_correct == null }) {
            activity.finish()
            return
        }

        val newList = testList.mapIndexed { index, qstRecord -> index to qstRecord }
                .filter { it.second.is_correct == null }
        if (newList.any { it.first > position }) {
            pager.currentItem = newList.first { it.first > position }.first
        } else {
            pager.currentItem = newList.first().first
        }
    }
}