package kr.yangbob.memorization.view

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_detail.*
import kr.yangbob.memorization.EXTRA_TO_QST_ID
import kr.yangbob.memorization.R
import kr.yangbob.memorization.adapter.DetailListAdapter
import kr.yangbob.memorization.databinding.ActivityDetailBinding
import kr.yangbob.memorization.viewmodel.QstViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailActivity : AppCompatActivity() {

    private val model: QstViewModel by viewModel()
    private var cancelMenu: MenuItem? = null
    private var editMenu: MenuItem? = null
    private var saveMenu: MenuItem? = null
    private var deleteMenu: MenuItem? = null
    private lateinit var deleteDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        model.setQstId(intent.getIntExtra(EXTRA_TO_QST_ID, -1))

        val binding: ActivityDetailBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_detail)
        binding.lifecycleOwner = this
        binding.model = model
        binding.qst = model.getQst()

        // textView scroll 설정
        qstDataText.movementMethod = ScrollingMovementMethod()
        answerDataText.movementMethod = ScrollingMovementMethod()

        // appbar 설정
        toolBar.title = getString(R.string.qst_appbar_title)
        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // grid column 개수를 위한 작업
        val displayMetrics: DisplayMetrics = resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        val noOfColumns = (screenWidthDp / 166).toInt()     // 166dp = card width(150 - 이상적인 걸로 판단) + margin(16)

        // 시험 기록 list 작업
        val recordList = model.getRecordList()
        val adapter = DetailListAdapter(recordList, model)
        recordRecycler.layoutManager = GridLayoutManager(this, noOfColumns)
        recordRecycler.adapter = adapter

        // dialog 생성
        deleteDialog = AlertDialog.Builder(this, R.style.DeleteDialog).setTitle(R.string.qst_delete_msg)
                .setPositiveButton(R.string.delete) { _, _ ->
                    val deleteList = model.delete()
                    setResult(RESULT_OK, Intent().apply {
                        putIntegerArrayListExtra("deleteList", deleteList)
                    })
                    finish()
                }.setNegativeButton(R.string.cancel) { _, _ -> }.create()
    }

    override fun onResume() {
        model.resetIsPossibleClick()
        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail, menu)
        editMenu = menu?.findItem(R.id.action_qst_edit)
        cancelMenu = menu?.findItem(R.id.action_qst_cancel)
        saveMenu = menu?.findItem(R.id.action_qst_save)
        deleteMenu = menu?.findItem(R.id.action_qst_delete)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        // up 버튼(HomeAsUp)을 눌렀을 때, 어느 페이지로 갈지 알 수 없으니 finish로 재정의
        android.R.id.home -> {
            finish()
            true
        }
        R.id.action_qst_delete -> {
            deleteDialog.show()
            true
        }
        R.id.action_qst_edit -> {
            item.isVisible = false
            deleteMenu?.isVisible = false
            saveMenu?.isVisible = true
            cancelMenu?.isVisible = true
            displayEdit()
            true
        }
        R.id.action_qst_save -> {
            if (model.isPossibleSave()) {
                item.isVisible = false
                cancelMenu?.isVisible = false
                editMenu?.isVisible = true
                deleteMenu?.isVisible = true
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
            deleteMenu?.isVisible = true
            model.cancel()
            displayText()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun displayText() {
        answerDataText.visibility = View.VISIBLE
        qstDataText.visibility = View.VISIBLE
        answerDataEdit.visibility = View.GONE
        qstDataEdit.visibility = View.GONE

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(answerDataText.windowToken, 0)
    }

    private fun displayEdit() {
        answerDataEdit.visibility = View.VISIBLE
        qstDataEdit.visibility = View.VISIBLE
        answerDataText.visibility = View.GONE
        qstDataText.visibility = View.GONE
    }
}