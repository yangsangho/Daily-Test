package kr.yangbob.memorization.view

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import kotlinx.android.synthetic.main.activity_add.*
import kr.yangbob.memorization.R
import kr.yangbob.memorization.databinding.ActivityAddBinding
import kr.yangbob.memorization.viewmodel.AddViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class AddActivity : AppCompatActivity() {

    private val model: AddViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityAddBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_add)
        binding.lifecycleOwner = this
        binding.model = model

        binding.qstData.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

        btnFinish.setOnClickListener {
            if (model.insertDataIsEmpty()) Toast.makeText(
                this,
                R.string.toast_need_input_qst,
                Toast.LENGTH_LONG
            ).show()
            else {
                imm.hideSoftInputFromWindow(it.windowToken, 0)
                model.insertQst()
                finish()
            }
        }
    }
}
