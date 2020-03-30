package kr.yangbob.memorization.view

import android.content.res.Configuration
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_tutorial_at_test.*
import kr.yangbob.memorization.EXTRA_TO_TUTORIAL
import kr.yangbob.memorization.R

class TutorialActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        setContentView( when(intent.getStringExtra(EXTRA_TO_TUTORIAL)){
            "entire" -> R.layout.activity_tutorial_at_entire
            "today" -> R.layout.activity_tutorial_at_today
            "test" -> R.layout.activity_tutorial_at_test
            "create" -> R.layout.activity_tutorial_at_create
            else -> throw IllegalArgumentException()
        })

        tutorialLayout.setOnClickListener {
            finish()
        }
    }
}
