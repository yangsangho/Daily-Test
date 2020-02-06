package kr.yangbob.memorization.view

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.DisplayMetrics
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_qst.*
import kr.yangbob.memorization.EXTRA_TO_QST_ID
import kr.yangbob.memorization.R
import kr.yangbob.memorization.databinding.ActivityQstBinding
import kr.yangbob.memorization.databinding.ItemRecordCardBinding
import kr.yangbob.memorization.db.QstRecord
import kr.yangbob.memorization.viewmodel.QstViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class QstActivity : AppCompatActivity() {

    private val model: QstViewModel by viewModel()
    private var cancelMenu: MenuItem? = null
    private var editMenu: MenuItem? = null
    private var saveMenu: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model.setQstId(intent.getIntExtra(EXTRA_TO_QST_ID, -1))

        val binding: ActivityQstBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_qst)
        binding.lifecycleOwner = this
        binding.model = model
        binding.qst = model.getQst()

        // textView scroll 설정
        qstDataText.movementMethod = ScrollingMovementMethod()
        answerDataText.movementMethod = ScrollingMovementMethod()

        // appbar 설정
        toolBar.title = resources.getString(R.string.qst_appbar_title)
        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // grid column 개수를 위한 작업
        val displayMetrics: DisplayMetrics = resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        val noOfColumns = (screenWidthDp / 166).toInt()     // 166dp = card width(150 - 이상적인 걸로 판단) + margin(16)

        // 시험 기록 list 작업
        val recordList = model.getRecordList()
        val adapter = QstRecyclerAdapter(recordList, model)
        recordRecycler.layoutManager = GridLayoutManager(this, noOfColumns)
        recordRecycler.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_qst, menu)
        editMenu = menu?.findItem(R.id.action_qst_edit)
        cancelMenu = menu?.findItem(R.id.action_qst_cancel)
        saveMenu = menu?.findItem(R.id.action_qst_save)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item.itemId){
        // up 버튼(HomeAsUp)을 눌렀을 때, 어느 페이지로 갈지 알 수 없으니 finish로 재정의
        android.R.id.home -> {
            finish()
            true
        }
        R.id.action_qst_edit -> {
            item.isVisible = false
            saveMenu?.isVisible = true
            cancelMenu?.isVisible = true
            displayEdit()
            true
        }
        R.id.action_qst_save -> {
            if(model.isPossibleSave()){
                item.isVisible = false
                cancelMenu?.isVisible = false
                editMenu?.isVisible = true
                model.save()
                displayText()
            } else {
                Toast.makeText(this, R.string.toast_need_input_qst, Toast.LENGTH_LONG).show()
            }
            true
        }
        R.id.action_qst_cancel -> {
            item.isVisible = false
            saveMenu?.isVisible = false
            editMenu?.isVisible = true
            model.cancel()
            displayText()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun displayText(){
        answerDataText.visibility = View.VISIBLE
        qstDataText.visibility = View.VISIBLE
        answerDataEdit.visibility = View.GONE
        qstDataEdit.visibility = View.GONE
    }
    private fun displayEdit(){
        answerDataEdit.visibility = View.VISIBLE
        qstDataEdit.visibility = View.VISIBLE
        answerDataText.visibility = View.GONE
        qstDataText.visibility = View.GONE
    }
}

class QstViewHolder(private val binding: ItemRecordCardBinding, private val model: QstViewModel) :
    RecyclerView.ViewHolder(binding.root) {
    fun onBind(record: QstRecord) {
        binding.record = record
    }
}

class QstRecyclerAdapter(private val recordList: List<QstRecord>, private val model: QstViewModel) :
    RecyclerView.Adapter<QstViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QstViewHolder {
        val binding: ItemRecordCardBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_record_card,
            parent,
            false
        )
        return QstViewHolder(binding, model)
    }

    override fun getItemCount(): Int = recordList.size

    override fun onBindViewHolder(holder: QstViewHolder, position: Int) {
        holder.onBind(recordList[position])
    }
}