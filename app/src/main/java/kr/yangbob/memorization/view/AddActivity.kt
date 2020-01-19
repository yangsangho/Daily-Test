package kr.yangbob.memorization.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import kotlinx.android.synthetic.main.activity_add.*
import kr.yangbob.memorization.R
import kr.yangbob.memorization.databinding.ActivityAddBinding
import kr.yangbob.memorization.viewmodel.CrudViewModel
import org.koin.android.ext.android.inject

class AddActivity : AppCompatActivity() {

    private val model: CrudViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityAddBinding = DataBindingUtil.setContentView(this, R.layout.activity_add)
        binding.lifecycleOwner = this
        binding.model = model

        doneBtn.setOnClickListener{
            if( model.insertDataIsEmpty() ) Toast.makeText(this, R.string.toast_need_input_qst, Toast.LENGTH_LONG).show()
            else {
                model.insertQst()
                finish()
            }
        }
    }
}
