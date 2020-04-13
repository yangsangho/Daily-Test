package kr.yangbob.memorization.viewmodel

import androidx.lifecycle.ViewModel

open class BaseViewModel : ViewModel() {
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