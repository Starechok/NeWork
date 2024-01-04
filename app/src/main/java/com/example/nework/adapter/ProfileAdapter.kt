package com.example.nework.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.nework.ui.JobsFragment
import com.example.nework.ui.WallFragment

class ProfileAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val itemCount = 2

    override fun getItemCount(): Int {
        return this.itemCount
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return WallFragment()
            1 -> return JobsFragment()
        }
        return Fragment()
    }
}