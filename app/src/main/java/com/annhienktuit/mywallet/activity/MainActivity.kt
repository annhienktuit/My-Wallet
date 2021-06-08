package com.annhienktuit.mywallet.activity

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

import androidx.fragment.app.Fragment

import com.annhienktuit.mywallet.R
import com.annhienktuit.mywallet.`object`.*
import com.annhienktuit.mywallet.fragments.HomeFragment
import com.annhienktuit.mywallet.fragments.PlanningFragment
import com.annhienktuit.mywallet.fragments.ReportFragment
import com.annhienktuit.mywallet.fragments.UserFragment
import com.annhienktuit.mywallet.utils.Extensions.toast
import com.annhienktuit.mywallet.utils.FirebaseUtils
import com.google.android.material.bottomnavigation.BottomNavigationView

import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_add_transaction.view.*
import kotlinx.android.synthetic.main.fragment_current_month.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


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
    private var savingList = ArrayList<Saving>()
    private var transactionList = ArrayList<RecentTransaction>()
    private var cardList = ArrayList<Card>()
    private var limitList = ArrayList<Limitation>()
    //-------------------------------------------------------
    private var name: String? = null
    private var income: String? = null
    private var expense: String? = null
    private var balance: String? = null
    private var totalTrans: Int = 0
    private var firstStart = false
    //--------------------------------
    private var currentMonthIncome: String? = null
    //--------------------------------
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
            window.statusBarColor = Color.parseColor("#FFFFFF")
        }
        ref.keepSynced(true)
        if(user == null) {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        getDatabase(ref, object : OnGetDataListener {
            override fun onSuccess(dataSnapshot: DataSnapshot) {
                setUpDatabase(dataSnapshot)
                setUI()
                if (!firstStart) {
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.container_fragment, HomeFragment())
                    //transaction.addToBackStack(null)
                    transaction.commit()
                    firstStart = true
                }
            }
            override fun onStart() {

            }
            override fun onFailure() {
            }
        })
        fab.setOnClickListener {
            eventOnClickAddButton()
        }
    }

    private fun eventOnClickAddButton() {
        val builder = AlertDialog.Builder(this)
        val viewInflater = LayoutInflater.from(this).inflate(R.layout.dialog_add_transaction, null, false)
        val textCategory = viewInflater.findViewById<AutoCompleteTextView>(R.id.textCategory)
        val editName = viewInflater.findViewById<TextInputEditText>(R.id.inputDetailTrans)
        val editMoney = viewInflater.findViewById<TextInputEditText>(R.id.inputMoneyTrans)
        val itemsIncome = resources.getStringArray(R.array.categoriesIncome)
        val itemsExpense = resources.getStringArray(R.array.categoriesExpense)
        var date = Calendar.getInstance()
        var dayFormatter = SimpleDateFormat("yyyy/MM/dd")
        var timeFormatter = SimpleDateFormat("hh:mm")
        var inorout = "true"
        var day = dayFormatter.format(date.time)
        var time = timeFormatter.format(date.time)
        val categoryAdapter2 = ArrayAdapter(applicationContext, R.layout.layout_category, itemsExpense)
        val categoryAdapter1 = ArrayAdapter(applicationContext, R.layout.layout_category, itemsIncome)
        viewInflater.toggleMoneyButton.check(R.id.toggleIncome)
        textCategory.setAdapter(categoryAdapter1)
        viewInflater.toggleMoneyButton.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                if (checkedId == R.id.toggleIncome){
                    textCategory.setText("")
                    inorout = "true"
                    textCategory.setAdapter(categoryAdapter1)
                }
                if (checkedId == R.id.toggleExpense) {
                    inorout = "false"
                    textCategory.setText("")
                    textCategory.setAdapter(categoryAdapter2)
                }
            }
        }

        val refTrans = ref.child(user?.uid.toString()).child("transactions").child("transaction" + (totalTrans + 1))
        val ref1 = ref.child(user?.uid.toString())
        textCategory.dropDownHeight = 500
        builder.setView(viewInflater)
        builder.setPositiveButton("OK"
        ) { dialog, which ->
            val name = editName.text.toString()
            val money = editMoney.text.toString()
            refTrans.child("day").setValue(day)
            refTrans.child("money").setValue(money)
            refTrans.child("name").setValue(name)
            refTrans.child("time").setValue(time)
            refTrans.child("inorout").setValue(inorout)
            refTrans.child("category").setValue(textCategory.text.toString())
            refTrans.child("currentMonth").setValue((date.get(Calendar.MONTH) + 1).toString())
            refTrans.child("currentYear").setValue(date.get(Calendar.YEAR)).toString()
            if (inorout == "true") {
                ref1.child("income").setValue((income?.toLong()?.plus(money.toLong())).toString())
                ref1.child("balance").setValue((balance?.toLong()?.plus(money.toLong())).toString())
            }
            else {
                ref1.child("expense").setValue((expense?.toLong()?.plus(money.toLong())).toString())
                ref1.child("balance").setValue((balance?.toLong()?.minus(money.toLong())).toString())
            }
        }
        builder.setNegativeButton("Cancel"
        ) { dialog, which -> dialog.cancel() }
        builder.show()
    }


    private fun setUI() {

        //modify the display of bottom navigation view
        bottomNavigationView.background = null
        bottomNavigationView.menu.getItem(2).isEnabled = false
        //----------------------------------------------
        val fragment = when (bottomNavigationView.selectedItemId) {
            R.id.navReport -> ReportFragment()
            R.id.navPlanning -> PlanningFragment()
            R.id.navUser -> UserFragment()
            else -> {
                HomeFragment()
            }
        }
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container_fragment, fragment)

        //--------------------------------------------
        if (user !== null) {
            toast("Hello ${user!!.email}")
        } else {
            Log.i("logged in", "false")
        }
        //--------------------------------------
        bottomNavigationView.setOnItemSelectedListener(mOnBottomNavigationView)
    }

    private val mOnBottomNavigationView = BottomNavigationView.OnNavigationItemSelectedListener {
        when (it.itemId) {
            R.id.navHome -> {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.container_fragment, HomeFragment())
                //transaction.addToBackStack(null)
                transaction.commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navReport -> {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.container_fragment, ReportFragment())
                //transaction.addToBackStack(null)
                transaction.commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navPlanning -> {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.container_fragment, PlanningFragment())
                //transaction.addToBackStack(null)
                transaction.commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navUser -> {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.container_fragment, UserFragment())
               // transaction.addToBackStack(null)
                transaction.commit()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
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
            val savingDb = db.child("savings")
            val transactionDb = db.child("transactions")
            val cardDb = db.child("cards")
            val limitDb = db.child("limits")
            name = data.child(user?.uid.toString()).child("name").value.toString()
            income = data.child(user?.uid.toString()).child("income").value.toString()
            expense = data.child(user?.uid.toString()).child("expense").value.toString()
            balance = data.child(user?.uid.toString()).child("balance").value.toString()
            getDatabase(savingDb, object : OnGetDataListener {
                override fun onSuccess(dataSnapshot: DataSnapshot) {
                    savingList.clear()
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
            getDatabase(transactionDb.orderByChild("day"), object : OnGetDataListener {
                override fun onSuccess(dataSnapshot: DataSnapshot) {
                    totalTrans = 0
                    transactionList.clear()
                    for (data in dataSnapshot.children) {
                        val day = data.child("day").value.toString()
                        val originalDay = SimpleDateFormat("yyyy/MM/dd")
                        val targetDay = SimpleDateFormat("dd/MM/yyyy")
                        val tmpDayOriginal = originalDay.parse(day)
                        val tmpDayTarget = targetDay.format(tmpDayOriginal)
                        var inorout = data.child("inorout").value.toString()
                        var money = data.child("money").value.toString()
                        var name = data.child("name").value.toString()
                        var time = data.child("time").value.toString()
                        transactionList.add(RecentTransaction(tmpDayTarget, inorout, money, name, time))
                        totalTrans++
                    }
                    transactionList.reverse()
                }
                override fun onStart() {
                }
                override fun onFailure() {
                }
            })
            getDatabase(cardDb, object : OnGetDataListener {
                override fun onSuccess(dataSnapshot: DataSnapshot) {
                    cardList.clear()
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
                    limitList.clear()
                    for (data in dataSnapshot.children) {
                        var nameLimit = data.child("nameLimit").value.toString()
                        var costLimit = data.child("costLimit").value.toString()
                        limitList.add(Limitation(costLimit, nameLimit))
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
    fun getDatabase(ref: Query, listener: OnGetDataListener?) {
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
    fun getSavingList(): ArrayList<Saving> {
        return savingList
    }
    fun getTransactionList(): ArrayList<RecentTransaction> {
        return transactionList
    }
    fun getCardList(): ArrayList<Card> {
        return cardList
    }
    fun getLimitationList(): ArrayList<Limitation> {
        return limitList
    }
}




