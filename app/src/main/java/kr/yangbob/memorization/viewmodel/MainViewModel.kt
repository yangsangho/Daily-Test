package kr.yangbob.memorization.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kr.yangbob.memorization.R
import kr.yangbob.memorization.db.Qst
import kr.yangbob.memorization.db.Test
import kr.yangbob.memorization.db.TestIncTitle
import kr.yangbob.memorization.model.MemRepository

class MainViewModel(application: Application): AndroidViewModel(application)
{
    private val memRepo = MemRepository(application)

    fun getAllQna(): List<Qst> = memRepo.getAllQnA()
    fun getAllTest(): List<Test> = memRepo.getAllTest()
    fun getAllIncTitle(): List<TestIncTitle> = memRepo.getAllIncTitle()

    val testText = application.resources.getText(R.string.dashboard_today_card3)
}