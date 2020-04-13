package kr.yangbob.memorization.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel

open class BaseAndroidViewModel(application: Application) : AndroidViewModel(application) {
    private var isPossibleClick = false

    fun resetIsPossibleClick() {
        isPossibleClick = false
    }

    fun checkIsPossibleClick(): Boolean = if (isPossibleClick) false
    else {
        isPossibleClick = true
        true
    }
}