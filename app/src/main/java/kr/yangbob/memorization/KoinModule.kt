package kr.yangbob.memorization

import androidx.room.Room
import kr.yangbob.memorization.db.MemDatabase
import kr.yangbob.memorization.model.MemRepository
import kr.yangbob.memorization.viewmodel.CrudViewModel
import kr.yangbob.memorization.viewmodel.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.util.*

val viewModelModule = module {
    viewModel {
        MainViewModel(get())
    }
    viewModel {
        CrudViewModel(get())
    }
    single { MemRepository( get() ) }
    single {
        Room.databaseBuilder(androidContext(),
            MemDatabase::class.java, "Memorization"
        ).fallbackToDestructiveMigration().build()
    }
}
val utilModule = module {
    factory {
        Calendar.getInstance().apply {
            set(Calendar.HOUR, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }
}