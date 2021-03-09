package com.example.instagramkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.instagramkotlin.fragments.AddPostFragment
import com.example.instagramkotlin.fragments.HomeFragment
import com.example.instagramkotlin.fragments.ProfileFragment
import com.example.instagramkotlin.fragments.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private var selectedFragment:Fragment? = null

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener{item ->
        when(item.itemId){
            R.id.nav_home ->{
                selectFragmentMethod(HomeFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_notification ->{
                selectFragmentMethod(NotificationFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_addpost ->{
                startActivity(Intent(this, AddNewPost::class.java))
                item.isChecked = false
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_search ->{
                selectFragmentMethod(SearchFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_profile ->{
                selectFragmentMethod(ProfileFragment())
                return@OnNavigationItemSelectedListener true
            }
        }
        if(selectedFragment != null){
            supportFragmentManager.beginTransaction().replace(R.id.main_frameLayout, selectedFragment!!).commit()
        }
        false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var navView:BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        selectFragmentMethod(HomeFragment())
    }

   private fun selectFragmentMethod(fragment: Fragment){
       supportFragmentManager.beginTransaction().replace(R.id.main_frameLayout, fragment).commit()
   }


}