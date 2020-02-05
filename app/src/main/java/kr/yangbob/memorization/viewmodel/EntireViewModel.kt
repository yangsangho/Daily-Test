package kr.yangbob.memorization.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import kr.yangbob.memorization.db.Qst
import kr.yangbob.memorization.model.MemRepository
import java.text.DateFormat

class EntireViewModel(private val memRepo: MemRepository) : ViewModel() {
    fun getAllQst(): LiveData<List<Qst>> = memRepo.getAllQstLD()
    fun getFormattedDate(dateStr: String): String =
        memRepo.getFormattedDate(dateStr, DateFormat.DEFAULT)
}