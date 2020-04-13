package kr.yangbob.memorization.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class MainFragmentAdapter(mainLifeCycle: Lifecycle, fm: FragmentManager) :
        FragmentStateAdapter(fm, mainLifeCycle) {
    override fun getItemCount(): Int = 2
    override fun createFragment(position: Int): Fragment =
            MainFragment.newInstance(position == 0)
}