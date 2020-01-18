package kr.yangbob.memorization

import androidx.room.Room
import kr.yangbob.memorization.db.MemDatabase
import kr.yangbob.memorization.model.MemRepository
import kr.yangbob.memorization.viewmodel.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val viewModelModule = module {
    viewModel {
        MainViewModel(get())
    }
    single { MemRepository( get() ) }
    single {
        Room.databaseBuilder(androidContext(),
            MemDatabase::class.java, "Memorization"
        ).fallbackToDestructiveMigration().build()
    }
}