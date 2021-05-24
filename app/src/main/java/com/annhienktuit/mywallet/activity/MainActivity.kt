package com.annhienktuit.mywallet.activity

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
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
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import java.io.Serializable


class MainActivity : AppCompatActivity() {
    //fragment declarations
    private val homeFragment = HomeFragment()
    private val reportFragment = ReportFragment()
    private val planningFragment = PlanningFragment()
    private val userFragment = UserFragment()
    val user: FirebaseUser? = FirebaseUtils.firebaseAuth.currentUser
    var async = AppDatabase()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(user == null) {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        else async.execute()
        var fullName = intent.getStringExtra("fulname").toString()
        async.fullName = fullName

        var tempDialog = ProgressDialog(this@MainActivity)
        tempDialog.setMessage("Please wait...")
        tempDialog.setCancelable(false)
        tempDialog.progress = 0
        tempDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        tempDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.GRAY))
        tempDialog.show()
        val mCountDownTimer = object : CountDownTimer(1700, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tempDialog.setMessage("Please wait...")
            }
            override fun onFinish() {
                tempDialog.dismiss()
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
        }
        mCountDownTimer.start()
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


    }

    class AppDatabase : AsyncTask<Void, Void, Void>() {
        val user: FirebaseUser? = FirebaseUtils.firebaseAuth.currentUser
        var fullName: String? = null
        set(value) {
            field = value
        }
        var db =  FirebaseDatabase
            .getInstance("https://my-wallet-80ed7-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("datas").child(user?.uid.toString())
        var walletDb = db.child("wallets")
        var savingDb = db.child("savings")
        var savingDetailDb = db.child("savings").child("details")
        var transactionDb = db.child("transactions")
        var cardDb = db.child("cards")
        var data : HashMap<String, String>? = null
        var walletList = ArrayList<Wallet>()
        var savingList = ArrayList<Saving>()
        var transactionList = ArrayList<RecentTransaction>()
        var cardList = ArrayList<Card>()
        override fun onPreExecute() {
            db.keepSynced(true)
            var db2 = FirebaseDatabase
                .getInstance("https://my-wallet-80ed7-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("datas")
            db2.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.hasChild(user!!.uid.toString())) {
                        db2.child(user!!.uid.toString()).child("name").setValue(fullName)
                        db2.child(user!!.uid.toString()).child("balance").setValue("0")
                        db2.child(user!!.uid.toString()).child("income").setValue("0")
                        db2.child(user!!.uid.toString()).child("expense").setValue("0")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("khaihoan", "failed")
                }
            })
        }
        override fun doInBackground(vararg params: Void?): Void? {
            try {
                db.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        data = snapshot.value as HashMap<String, String>?
                        Log.d("khaidoin", "success" + data)
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Log.d("khaihoan", "failed")
                    }
                })
                Thread.sleep(1500)
                walletDb.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (data in snapshot.children) {
                            var model = walletDb.child(data.key.toString())
                            model.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    var tmp1 = snapshot.child("nameWallet").value.toString()
                                    var tmp2 = snapshot.child("balanceWallet").value.toString()
                                    walletList.add(Wallet(tmp2, tmp1))
                                }
                                override fun onCancelled(error: DatabaseError) {
                                    Log.d("khaihoan", "failed")
                                }
                            })
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Log.d("khaihoan", "failed")
                    }
                })
                savingDb.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (data in snapshot.children) {
                            var model = savingDb.child(data.key.toString())
                            model.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    var tmp1 = snapshot.child("current").value.toString()
                                    var tmp2 = snapshot.child("price").value.toString()
                                    var tmp3 = snapshot.child("product").value.toString()
                                    var tmp4 = ArrayList<SavingDetail>()
                                    savingDetailDb.addValueEventListener(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            var t1 = snapshot.child("cost").value.toString()
                                            var t2 = snapshot.child("day").value.toString()
                                            var t3 = snapshot.child("time").value.toString()
                                            var t4 = snapshot.child("transName").value.toString()
                                            tmp4.add(SavingDetail(t1, t2, t3, t4))
                                        }
                                        override fun onCancelled(error: DatabaseError) {
                                            Log.d("khaihoan", "failed")
                                        }
                                    })
                                    savingList.add(Saving(tmp1,tmp4, tmp2, tmp3))
                                }
                                override fun onCancelled(error: DatabaseError) {
                                    Log.d("khaihoan", "failed")
                                }
                            })
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Log.d("khaihoan", "failed")
                    }
                })
                transactionDb.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (data in snapshot.children) {
                            var model = transactionDb.child(data.key.toString())
                            model.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    var tmp1 = data.child("day").value.toString()
                                    var tmp2 = data.child("inorout").value.toString()
                                    var tmp3 = data.child("money").value.toString()
                                    var tmp4 = data.child("name").value.toString()
                                    var tmp5 = data.child("time").value.toString()
                                    transactionList.add(RecentTransaction(tmp1, tmp2, tmp3, tmp4, tmp5))
                                }
                                override fun onCancelled(error: DatabaseError) {
                                    Log.d("khaihoan", "failed")
                                }
                            })
                        }

                    }
                    override fun onCancelled(error: DatabaseError) {
                        Log.d("khaihoan", "failed")
                    }
                })
                cardDb.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (data in snapshot.children) {
                            var model = cardDb.child(data.key.toString())
                            model.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    var tmp1 = data.child("accountNumber").value.toString()
                                    var tmp2 = data.child("bankName").value.toString()
                                    var tmp3 = data.child("cardNumber").value.toString()
                                    var tmp4 = data.child("expiredDate").value.toString()
                                    var tmp5 = data.child("name").value.toString()
                                    var tmp6 = data.child("namePerson").value.toString()
                                    cardList.add(Card(tmp1, tmp2, tmp3, tmp4, tmp5, tmp6))
                                }
                                override fun onCancelled(error: DatabaseError) {
                                    Log.d("khaihoan", "failed")
                                }
                            })
                        }

                    }
                    override fun onCancelled(error: DatabaseError) {
                        Log.d("khaihoan", "failed")
                    }
                })
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            return null

        }

        fun getDatas(): HashMap<String, String>? {
            return data
        }
        fun getWallets(): ArrayList<Wallet> {
            return walletList
        }
        fun getSavings(): ArrayList<Saving> {
            return savingList
        }
        fun getTransactions(): ArrayList<RecentTransaction> {
            return transactionList
        }
        fun getCards(): ArrayList<Card> {
            return cardList
        }
    }
    fun getData(): HashMap<String, String>? {
        return async.getDatas()
    }
    fun getWalletList(): ArrayList<Wallet>? {
        return async.getWallets()
    }
    fun getSavingList(): ArrayList<Saving> {
        return async.getSavings()
    }
    fun getTransactionList(): ArrayList<RecentTransaction> {
        return async.getTransactions()
    }
    fun getCardList(): ArrayList<Card> {
        return async.getCards()
    }
}

