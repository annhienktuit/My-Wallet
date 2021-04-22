package com.annhienktuit.mywallet

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.annhienktuit.mywallet.fragments.HomeFragment
import com.annhienktuit.mywallet.fragments.PlanningFragment
import com.annhienktuit.mywallet.fragments.ReportFragment
import com.annhienktuit.mywallet.fragments.UserFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    //fragment declarations
    private val homeFragment = HomeFragment()
    private val reportFragment = ReportFragment()
    private val planningFragment = PlanningFragment()
    private val userFragment = UserFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //hide action bar
        if (supportActionBar != null) {
            supportActionBar!!.hide();
        }
        //---------------

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.parseColor("#4CAF50")
        }

        //modify the display of bottom navigation view
        bottomNavigationView.background = null
        bottomNavigationView.menu.getItem(2).isEnabled = false
        //--------------------------------------------

        replaceFragment(homeFragment) // Home is the first fragment displayed

        bottomNavigationView.setOnNavigationItemSelectedListener{
            when(it.itemId){
                R.id.navHome -> replaceFragment(homeFragment)
                R.id.navReport -> replaceFragment(reportFragment)
                R.id.navPlanning -> replaceFragment(planningFragment)
                R.id.navUser -> replaceFragment(userFragment)
            }
            true //return type
        }
    }

    private fun replaceFragment(fragment: Fragment){
        if(fragment != null){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, fragment)
            transaction.commit()
        }
    }
}
