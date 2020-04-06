package kr.yangbob.memorization.test

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

class FakeRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application {
        return super.newApplication(cl, FakeApplication::class.java.name, context)
    }
}