package kr.yangbob.memorization.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_calendar.*
import kr.yangbob.memorization.R

class CalendarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        calendarView
    }
}

class CalendarRecycler(private val activity: CalendarActivity) : RecyclerView.Adapter<>(){

}