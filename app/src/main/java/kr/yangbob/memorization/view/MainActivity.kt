package kr.yangbob.memorization.view

import android.os.Bundle
import android.text.format.DateFormat
import androidx.appcompat.app.AppCompatActivity
import kr.yangbob.memorization.R
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DateFormat.getBestDateTimePattern(Locale.KOREAN, "")
    }
}
