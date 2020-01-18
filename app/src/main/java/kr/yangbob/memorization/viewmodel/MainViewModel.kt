package kr.yangbob.memorization.viewmodel

import androidx.lifecycle.ViewModel
import kr.yangbob.memorization.db.Qst
import kr.yangbob.memorization.db.Test
import kr.yangbob.memorization.db.TestIncTitle
import kr.yangbob.memorization.model.MemRepository

class MainViewModel(val memRepo: MemRepository): ViewModel()
{
    fun getAllQna(): List<Qst> = memRepo.getAllQnA()
    fun getAllTest(): List<Test> = memRepo.getAllTest()
    fun getAllIncTitle(): List<TestIncTitle> = memRepo.getAllIncTitle()
}
