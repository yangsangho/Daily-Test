package kr.yangbob.memorization.view

import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
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
        if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        val binding: ActivityAddBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_add)
        binding.lifecycleOwner = this
        binding.model = model

        toolBar.title = getString(R.string.add_appbar_title)
        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.qstData.requestFocus()
    }

    override fun onResume() {
        model.resetIsPossibleClick()
        super.onResume()
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
            if(model.checkIsPossibleClick()){
                if(model.isPossibleInsert()){
                    model.insertQst()
                    finish()
                } else {
                    Toast.makeText(this, R.string.toast_need_input_qst, Toast.LENGTH_LONG).show()
                }
            }
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
