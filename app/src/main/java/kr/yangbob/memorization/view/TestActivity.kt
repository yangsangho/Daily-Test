package kr.yangbob.memorization.view

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_test.*
import kr.yangbob.memorization.*
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

        if (intent.getBooleanExtra(EXTRA_TO_TEST_FIRST, false)) {
            startActivity(Intent(this, TutorialActivity::class.java).apply {
                putExtra(EXTRA_TO_TUTORIAL, "test")
            })
        }

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        setContentView(R.layout.activity_test)

        val testRecordList: List<QstRecord>
        model.isDormant = intent.getBooleanExtra("isDormant", false)
        if (model.isDormant) {
            toolBar.title = getString(R.string.test_dormant_appbar_title)
            val partitionList =
                    model.getAllDormantQst().partition { it.cur_stage <= Stage.BEGIN_THREE.ordinal }
            // BEGIN_TWO 이하는 초기화
            partitionList.first.forEach {
                it.is_dormant = false
                it.cur_stage = 0
                it.next_test_date = model.getDateStr(todayTime + MILLIS_A_DAY)
                model.insertQst(it)
            }
            testRecordList = partitionList.second.map { QstRecord(it.id!!, "", it.cur_stage) }

            if (testRecordList.isEmpty()) {
                Toast.makeText(this, R.string.test_dormant_initialize_msg, Toast.LENGTH_LONG).show()
                finish()
            } else {
                val snackBar = Snackbar.make(
                        testLayout,
                        R.string.test_dormant_snackbar_msg,
                        Snackbar.LENGTH_INDEFINITE
                )
                snackBar.setAction(R.string.confirmation) {
                    snackBar.dismiss()
                }
                snackBar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).maxLines =
                        3
                snackBar.show()
            }
        } else {
            toolBar.title = getString(R.string.test_appbar_title)
            testRecordList = model.getTodayNullRecords()
        }
        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewPager.adapter = TestPagerAdapter(testRecordList.shuffled(), model, viewPager, this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        model.resetIsPossibleClick()
        super.onResume()
    }
}

class TestViewHolder(private val model: TestViewModel, private val binding: ItemTestViewpageBinding, private val adapter: TestPagerAdapter, private val qstSize: Int) : RecyclerView.ViewHolder(binding.root) {
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

    fun onBind(qstRecord: QstRecord, position: Int) {
        Log.i("TEST", "onBind($position)")
        this.qstRecord = qstRecord
        this.qst = model.getQstFromId(qstRecord.qst_id)
        binding.tvQstCnt.text = "${position + 1}/$qstSize"
        binding.qstProgress.max = qstSize
        binding.qstProgress.progress = position + 1

        binding.strData = qst.title
        binding.isFront = true
        binding.stage = qstRecord.challenge_stage
        tvQstAnswer.rotationY = 0f
        correctChkIcon.rotationY = 0f
        qnaIcon.rotationY = 0f
        stageIcon.rotationY = 0f
        card.rotationY = 0f
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

    fun clickChk(view: View) {
        if (!model.checkIsPossibleClick()) return

        val isCorrect = view.id == R.id.btnSuccessLayout
        if (binding.correct == isCorrect) {
            binding.correct = null
        } else {
            binding.correct = isCorrect
        }

        val goMove = if (model.isDormant) model.updateDormant(qst, qstRecord, isCorrect)
        else model.update(qst, qstRecord, isCorrect)

        if (goMove) adapter.move(adapterPosition)
        Handler().postDelayed({
            model.resetIsPossibleClick()
        }, 600)
    }
}

class TestPagerAdapter(private val testList: List<QstRecord>, private val model: TestViewModel, private val pager: ViewPager2, private val activity: Activity) : RecyclerView.Adapter<TestViewHolder>() {
    override fun getItemCount(): Int = testList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        val binding: ItemTestViewpageBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_test_viewpage,
                parent,
                false
        )
        return TestViewHolder(model, binding, this, testList.size)
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        holder.onBind(testList[position], position)
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
