package com.annhienktuit.mywallet.activity

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.annhienktuit.mywallet.R
import com.annhienktuit.mywallet.`object`.*
import com.annhienktuit.mywallet.adapter.MainPagerAdapter
import com.annhienktuit.mywallet.fragments.HomeFragment
import com.annhienktuit.mywallet.fragments.PlanningFragment
import com.annhienktuit.mywallet.fragments.ReportFragment
import com.annhienktuit.mywallet.fragments.UserFragment
import com.annhienktuit.mywallet.utils.Extensions.toast
import com.annhienktuit.mywallet.utils.FirebaseUtils
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    //fragment declarations
    private val homeFragment = HomeFragment()
    private val reportFragment = ReportFragment()
    private val planningFragment = PlanningFragment()
    private val userFragment = UserFragment()
    val user: FirebaseUser? = FirebaseUtils.firebaseAuth.currentUser
    var ref = FirebaseDatabase
        .getInstance("https://my-wallet-80ed7-default-rtdb.asia-southeast1.firebasedatabase.app/")
        .getReference("datas")
    //-------------------------------------------------------
    private var walletList = ArrayList<Wallet>()
    private var savingList = ArrayList<Saving>()
    private var transactionList = ArrayList<RecentTransaction>()
    private var cardList = ArrayList<Card>()
    private var limitList = ArrayList<Limitation>()
    //-------------------------------------------------------
    private var name: String? = null
    private var income: String? = null
    private var expense: String? = null
    private var balance: String? = null
    //--------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ref.keepSynced(true)
        if(user == null) {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        getDatabase(ref, object : OnGetDataListener {
            override fun onSuccess(dataSnapshot: DataSnapshot) {
                setUpDatabase(dataSnapshot)
                setUI()
            }
            override fun onStart() {

            }
            override fun onFailure() {
            }
        })

    }
    fun setUI() {
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
        //--------------------------------------
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
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navHome -> containerViewPager.currentItem = 0
                R.id.navReport -> containerViewPager.currentItem = 1
                R.id.navPlanning -> containerViewPager.currentItem = 2
                R.id.navUser -> containerViewPager.currentItem = 3
            }
            true
        }
    }
    fun setUpDatabase(data: DataSnapshot?) {
        if (!data?.hasChild(user?.uid.toString())!!) {
            val fullName = intent.getStringExtra("fulname").toString()
            ref.child(user?.uid.toString()).child("name").setValue(fullName)
            ref.child(user?.uid.toString()).child("balance").setValue("0")
            ref.child(user?.uid.toString()).child("income").setValue("0")
            ref.child(user?.uid.toString()).child("expense").setValue("0")
        } else {
            val db = ref.child(user?.uid.toString())
            val walletDb = db.child("wallets")
            val savingDb = db.child("savings")
            val transactionDb = db.child("transactions")
            val cardDb = db.child("cards")
            val limitDb = db.child("limits")
            name = data.child(user?.uid.toString()).child("name").value.toString()
            income = data.child(user?.uid.toString()).child("income").value.toString()
            expense = data.child(user?.uid.toString()).child("expense").value.toString()
            balance = data.child(user?.uid.toString()).child("balance").value.toString()
            getDatabase(walletDb, object : OnGetDataListener {
                override fun onSuccess(dataSnapshot: DataSnapshot) {
                    for (data in dataSnapshot.children) {
                        var nameWallet = data.child("nameWallet").value.toString()
                        var balanceWallet = data.child("balanceWallet").value.toString()
                        walletList.add(Wallet(balanceWallet, nameWallet))
                    }
                }
                override fun onStart() {
                }
                override fun onFailure() {
                }
            })
            getDatabase(savingDb, object : OnGetDataListener {
                override fun onSuccess(dataSnapshot: DataSnapshot) {
                    for (data in dataSnapshot.children) {
                        var current = data.child("current").value.toString()
                        var price = data.child("price").value.toString()
                        var product = data.child("product").value.toString()
                        savingList.add(Saving(current, null, price, product))
                    }
                }
                override fun onStart() {
                }
                override fun onFailure() {
                }
            })
            getDatabase(transactionDb, object : OnGetDataListener {
                override fun onSuccess(dataSnapshot: DataSnapshot) {
                    for (data in dataSnapshot.children) {
                        var day = data.child("day").value.toString()
                        var inorout = data.child("inorout").value.toString()
                        var money = data.child("money").value.toString()
                        var name = data.child("name").value.toString()
                        var time = data.child("time").value.toString()
                        transactionList.add(RecentTransaction(day, inorout, money, name, time))
                    }
                }
                override fun onStart() {
                }
                override fun onFailure() {
                }
            })
            getDatabase(cardDb, object : OnGetDataListener {
                override fun onSuccess(dataSnapshot: DataSnapshot) {
                    for (data in dataSnapshot.children) {
                        var accNum = data.child("accountNumber").value.toString()
                        var bankName = data.child("bankName").value.toString()
                        var cardNum = data.child("cardNumber").value.toString()
                        var date = data.child("expiredDate").value.toString()
                        var name = data.child("name").value.toString()
                        var namePerson = data.child("namePerson").value.toString()
                        cardList.add(Card(accNum, bankName, cardNum, date, name, namePerson))
                    }
                }
                override fun onStart() {
                }
                override fun onFailure() {
                }
            })
            getDatabase(limitDb, object : OnGetDataListener {
                override fun onSuccess(dataSnapshot: DataSnapshot) {
                    for (data in dataSnapshot.children) {
                        var nameLimit = data.child("nameLimit").value.toString()
                        var costLimit = data.child("costLimit").value.toString()
                        var cost = costLimit.toLong()
                        limitList.add(Limitation(cost, nameLimit))
                    }
                }
                override fun onStart() {
                }
                override fun onFailure() {
                }
            })
        }
    }
    interface OnGetDataListener {
        fun onSuccess(dataSnapshot: DataSnapshot)
        fun onStart()
        fun onFailure()
    }
    fun getDatabase(ref: DatabaseReference, listener: OnGetDataListener?) {
        listener?.onStart()
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                listener?.onSuccess(snapshot)
            }
            override fun onCancelled(error: DatabaseError) {
                listener?.onFailure()
            }
        })
    }

    fun getName(): String? {
        return name
    }
    fun getIncome(): String? {
        return income
    }
    fun getExpense(): String? {
        return expense
    }
    fun getBalance(): String? {
        return balance
    }
    fun getWalletList(): ArrayList<Wallet>? {
        return walletList
    }
    fun getSavingList(): ArrayList<Saving> {
        return savingList
    }
    fun getTransactionList(): ArrayList<RecentTransaction> {
        return transactionList
    }
    fun getCardList(): ArrayList<Card> {
        return cardList
    }
}

