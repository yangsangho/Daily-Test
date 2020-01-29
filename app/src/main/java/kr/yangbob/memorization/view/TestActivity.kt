package kr.yangbob.memorization.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.activity_test.*
import kr.yangbob.memorization.ANIMATION_FULL_TIME
import kr.yangbob.memorization.ANIMATION_HALF_TIME
import kr.yangbob.memorization.R
import kr.yangbob.memorization.databinding.ItemTestViewpageBinding
import kr.yangbob.memorization.db.Qst
import kr.yangbob.memorization.viewmodel.TestViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

const val TestActivityLogTag = "TestActivity"

class TestActivity : AppCompatActivity() {
    private val model: TestViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        val testList = model.getTestList()
        viewPager.adapter = TestPagerAdpater(testList, model)
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
    }
}

class TestViewHolder(
    private val model: TestViewModel,
    private val binding: ItemTestViewpageBinding
) : RecyclerView.ViewHolder(binding.root) {
    private val card = binding.card
    private val tvQstAnswer = binding.tvQstAnswer
    private lateinit var qst: Qst
    private var isFront = true

    init {
        binding.holder = this
    }

    fun onBind(qst: Qst) {
        this.qst = qst
        binding.strData = qst.title
        binding.isFront = isFront
    }

    // 애니메이션 적용
    fun clickShow(view: View) {
        card.cameraDistance = (10 * binding.card.width).toFloat()
        if (isFront) {
            tvQstAnswer.animate().setDuration(ANIMATION_HALF_TIME).alpha(1.0f)
                .withEndAction {
                    binding.strData = qst.answer
                    tvQstAnswer.rotationY = -180f
                }
            card.animate().setDuration(ANIMATION_FULL_TIME).rotationY(-180f)
        } else {
            tvQstAnswer.animate().setDuration(ANIMATION_HALF_TIME).alpha(1.0f)
                .withEndAction {
                    binding.strData = qst.title
                    tvQstAnswer.rotationY = 0f
                }
            card.animate().setDuration(ANIMATION_FULL_TIME).rotationY(0f)
        }
        isFront = !isFront
        binding.isFront = isFront
    }

    fun clickSuccess(view: View) {

    }

    fun clickFail(view: View) {

    }
}

class TestPagerAdpater(private val testList: List<Qst>, private val model: TestViewModel) :
    RecyclerView.Adapter<TestViewHolder>() {
    override fun getItemCount(): Int = testList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        Log.i(TestActivityLogTag, "onCreateViewHolder()")
        val binding: ItemTestViewpageBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_test_viewpage,
            parent,
            false
        )
        return TestViewHolder(model, binding)
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        Log.i(TestActivityLogTag, "onBindViewHolder()")
        holder.onBind(testList[position])
    }
}
