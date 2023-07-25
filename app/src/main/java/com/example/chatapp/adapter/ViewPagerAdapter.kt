package com.example.chatapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.chatapp.fragment.CallFragment
import com.example.chatapp.fragment.ChatFragment
import com.example.chatapp.fragment.UserProfile

class ViewPagerAdapter(fragmentManager: FragmentManager,lifecycle: Lifecycle):FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
       return  when(position){
           0->{
               ChatFragment()

           }
           1->{
               CallFragment()
           }
           2->{
               UserProfile()
           }
           else->{
               Fragment()
           }
       }
    }

}