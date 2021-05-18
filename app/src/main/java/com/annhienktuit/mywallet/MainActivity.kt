package com.annhienktuit.mywallet

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.annhienktuit.mywallet.`object`.RecentTransaction
import com.annhienktuit.mywallet.`object`.Wallet
import com.annhienktuit.mywallet.adapter.MainPagerAdapter
import com.annhienktuit.mywallet.fragments.HomeFragment
import com.annhienktuit.mywallet.fragments.PlanningFragment
import com.annhienktuit.mywallet.fragments.ReportFragment
import com.annhienktuit.mywallet.fragments.UserFragment
import com.annhienktuit.mywallet.utils.Extensions.toast
import com.annhienktuit.mywallet.utils.FirebaseUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    //fragment declarations
    private val homeFragment = HomeFragment()
    private val reportFragment = ReportFragment()
    private val planningFragment = PlanningFragment()
    private val userFragment = UserFragment()
    var walletList = ArrayList<Wallet>()
    var transactionList = ArrayList<RecentTransaction>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val user: FirebaseUser? = FirebaseUtils.firebaseAuth.currentUser
        if(user == null) {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        addWallet()
        addTransaction()
        //hide action bar
        if (supportActionBar != null) {
            supportActionBar!!.hide();
        }
        //---------------

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.parseColor("#FFFFFF")
        }

        //modify the display of bottom navigation view
        bottomNavigationView.background = null
        bottomNavigationView.menu.getItem(2).isEnabled = false
        //--------------------------------------------
        if (user !== null) {
            toast("Hello ${user!!.email}")
        } else {
            Log.i("logged in", "false")
        }

        //Fragment transition by view pager
        var adapter = MainPagerAdapter(
            supportFragmentManager,
            FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        )
        containerViewPager.adapter = adapter

        //When slide or choose a specific fragment, bottom navigation view icons will change their color to corresponding fragment
        containerViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> bottomNavigationView.menu.findItem(R.id.navHome).isChecked = true
                    1 -> bottomNavigationView.menu.findItem(R.id.navReport).isChecked = true
                    2 -> bottomNavigationView.menu.findItem(R.id.navPlanning).isChecked = true
                    3 -> bottomNavigationView.menu.findItem(R.id.navUser).isChecked = true
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        bottomNavigationView.setOnNavigationItemSelectedListener(object :
            BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.navHome -> containerViewPager.currentItem = 0
                    R.id.navReport -> containerViewPager.currentItem = 1
                    R.id.navPlanning -> containerViewPager.currentItem = 2
                    R.id.navUser -> containerViewPager.currentItem = 3
                }
                return true
            }
        })

    }

    fun addWallet() {
        walletList.add(Wallet("Food", "15,000"))
        walletList.add(Wallet("Clothing", "651,500"))
        walletList.add(Wallet("Parking", "100,000"))
        walletList.add(Wallet("Electricity", "496,124"))
        walletList.add(Wallet("Water", "122,650"))
        walletList.add(Wallet("Internet", "165,000"))
    }

    fun addTransaction() {
        transactionList.add(RecentTransaction("Food and Drink", "Today - Le Khai Hoan", "-20,000"))
        transactionList.add(
            RecentTransaction(
                "Shopping",
                "Yesterday - Tran Thanh Hien",
                "-179,000"
            )
        )
        transactionList.add(
            RecentTransaction(
                "Game",
                "Yesterday - Nguyen Huu An Nhien",
                "+220,000"
            )
        )
    }

    fun getWalletList(): List<Wallet> {
        return walletList
    }
    fun getTransactionList(): List<RecentTransaction> {
        return transactionList
    }
}
