package kr.yangbob.memorization.view

import android.app.Activity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
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
import kr.yangbob.memorization.db.QstRecord
import kr.yangbob.memorization.viewmodel.TestViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

const val TestActivityLogTag = "TestActivity"

class TestActivity : AppCompatActivity() {
    private val model: TestViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        toolBar.title = resources.getString(R.string.test_appbar_title)
        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val todayRecords = model.getTodayNullRecords()
        viewPager.adapter = TestPagerAdapter(todayRecords.shuffled(), model, viewPager, this)
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
    }
}

class TestViewHolder(
    private val model: TestViewModel,
    private val binding: ItemTestViewpageBinding,
    private val adapter: TestPagerAdapter
) : RecyclerView.ViewHolder(binding.root) {
    private val card = binding.card
    private val tvQstAnswer = binding.tvQstAnswer.apply {
        movementMethod = ScrollingMovementMethod()
    }
    private val correctChkIcon = binding.correctChkIcon
    private val qnaIcon = binding.qnaIcon
    private val stageIcon = binding.stageIcon

    private lateinit var qstRecord: QstRecord
    private lateinit var qst: Qst

    init {
        binding.holder = this
    }

    fun onBind(qstRecord: QstRecord) {
        this.qstRecord = qstRecord
        this.qst = model.getQstFromId(qstRecord.qst_id)
        binding.strData = qst.title
        binding.isFront = true
        binding.stage = qstRecord.challenge_stage
        if (qstRecord.is_correct != null) {
            binding.correct = qstRecord.is_correct
        } else {
            binding.correct = null
        }
    }

    // 애니메이션 적용
    fun clickShow(view: View) {
        card.cameraDistance = (10 * card.width).toFloat()
        if (binding.isFront!!) {
            tvQstAnswer.animate().setDuration(ANIMATION_HALF_TIME).alpha(1.0f)
                .withEndAction {
                    binding.strData = qst.answer
                    tvQstAnswer.rotationY = -180f
                    correctChkIcon.rotationY = -180f
                    qnaIcon.rotationY = -180f
                    stageIcon.rotationY = -180f
                    binding.isFront = false
                }
            card.animate().setDuration(ANIMATION_FULL_TIME).rotationY(-180f)
        } else {
            tvQstAnswer.animate().setDuration(ANIMATION_HALF_TIME).alpha(1.0f)
                .withEndAction {
                    binding.strData = qst.title
                    tvQstAnswer.rotationY = 0f
                    correctChkIcon.rotationY = 0f
                    qnaIcon.rotationY = 0f
                    stageIcon.rotationY = 0f
                    binding.isFront = true
                }
            card.animate().setDuration(ANIMATION_FULL_TIME).rotationY(0f)
        }
    }

    fun clickChk(view: View){
        val isCorrect = view.id == R.id.btnChkSuccess
        binding.correct?.let {
            if(it == isCorrect) return
        }
        binding.correct = isCorrect
        model.update(qst, qstRecord, isCorrect)
        adapter.move(adapterPosition)
    }
}

class TestPagerAdapter(private val testList: List<QstRecord>, private val model: TestViewModel, private val pager: ViewPager2, private val activity: Activity) :
    RecyclerView.Adapter<TestViewHolder>() {
    override fun getItemCount(): Int = testList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        val binding: ItemTestViewpageBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_test_viewpage,
            parent,
            false
        )
        return TestViewHolder(model, binding, this)
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        holder.onBind(testList[position])
    }

    fun move(position: Int){
        if(!testList.any { it.is_correct == null }){
            activity.finish()
            return
        }

        val newList = testList.mapIndexed{index, qstRecord -> index to qstRecord }.filter { it.second.is_correct == null }
        if(newList.any { it.first > position }){
            pager.currentItem = newList.first { it.first > position }.first
        } else {
            pager.currentItem = newList.first().first
        }
    }
}
