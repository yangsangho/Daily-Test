package kr.yangbob.memorization.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
    private var menu: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityAddBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_add)
        binding.lifecycleOwner = this
        binding.model = model

        toolBar.title = resources.getString(R.string.add_appbar_title)
        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.qstData.requestFocus()

// menu default로 enable false로 두고
//        val menuObserver = Observer<String> {
//            menu?.isEnabled = !model.insertDataIsEmpty()
//        }
//        model.answer.observe(this, menuObserver)
//        model.title.observe(this, menuObserver)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        this.menu = menu?.findItem(R.id.action_add_save)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item.itemId){
        android.R.id.home -> {
            finish()
            true
        }
        R.id.action_add_save -> {
            if(model.isPossibleInsert()){
                model.insertQst()
                finish()
            } else {
                Toast.makeText(this, R.string.toast_need_input_qst, Toast.LENGTH_LONG).show()
            }
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
