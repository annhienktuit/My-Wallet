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
import com.annhienktuit.mywallet.adapter.CardAdapter
import com.annhienktuit.mywallet.adapter.LimitationAdapter
import com.annhienktuit.mywallet.adapter.RecentTransactionAdapter
import com.annhienktuit.mywallet.adapter.SavingAdapter
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
import kotlinx.android.synthetic.main.dialog_done_interest_rate.*
import kotlinx.android.synthetic.main.fragment_current_month.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    val user: FirebaseUser? = FirebaseUtils.firebaseAuth.currentUser
    var ref = FirebaseDatabase
        .getInstance("https://my-wallet-80ed7-default-rtdb.asia-southeast1.firebasedatabase.app/")
        .getReference("datas")
    //-------------------------------------------------------
    private var savingList = ArrayList<Saving>()
    private var transactionList = ArrayList<RecentTransaction>()
    private var cardList = ArrayList<Card>()
    private var limitList = ArrayList<Limitation>()
    private var checked: Boolean = true
    //--------------------------------------------------
    private val savingAdapter = SavingAdapter(savingList)
    private val limitationAdapter = LimitationAdapter(limitList)
    private val cardAdapter = CardAdapter(cardList)
    private val transactionAdapter = RecentTransactionAdapter(transactionList)
    //-------------------------------------------------------
    private var name: String? = null
    private var income: String? = null
    private var expense: String? = null
    private var balance: String? = null
    private var totalTrans: Int = 0
    //-----------------------------------
    private var indexSaving: Int = 0
    private var indexCard: Int = 0
    private var indexLimitation: Int = 0
    //--------------------------------
    private var currentMonthIncome: String? = null
    //--------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ref.keepSynced(true)

        getDatabase(ref, object : OnGetDataListener {
            override fun onSuccess(dataSnapshot: DataSnapshot) {
                if(user == null) {
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                } else {
                    if (!dataSnapshot.hasChild(user.uid)) {
                        val fullName = intent.getStringExtra("fulname").toString()
                        ref.child(user.uid).child("name").setValue(fullName)
                        ref.child(user.uid).child("limits").child("total").setValue(0)
                        ref.child(user.uid).child("savings").child("total").setValue(0)
                        ref.child(user.uid).child("cards").child("total").setValue(0)
                        ref.child(user.uid).child("balance").setValue("0")
                        ref.child(user.uid).child("income").setValue("0")
                        ref.child(user.uid).child("expense").setValue("0")
                    }

                }
                setUpDatabase(dataSnapshot)
                if (checked) setUI()
                fab.setOnClickListener {
                    eventOnClickAddButton()
                }
            }
            override fun onStart() {

            }
            override fun onFailure() {
            }
        })

    }

    private fun eventOnClickAddButton() {
        val builder = AlertDialog.Builder(this)
        val viewInflater = LayoutInflater.from(this).inflate(R.layout.dialog_add_transaction, null, false)
        val textCategory = viewInflater.findViewById<AutoCompleteTextView>(R.id.textCategory)
        val editName = viewInflater.findViewById<TextInputEditText>(R.id.inputDetailTrans)
        val editMoney = viewInflater.findViewById<TextInputEditText>(R.id.inputMoneyTrans)
        val itemsIncome = resources.getStringArray(R.array.categoriesIncome)
        val itemsExpense = resources.getStringArray(R.array.categoriesExpense)
        val date = Calendar.getInstance()
        val dayFormatter = SimpleDateFormat("yyyy/MM/dd")
        val timeFormatter = SimpleDateFormat("HH:mm")
        var inorout = "true"
        val day = dayFormatter.format(date.time)
        val time = timeFormatter.format(date.time)
        val categoryAdapter2 = ArrayAdapter(applicationContext, R.layout.layout_category, itemsExpense)
        val categoryAdapter1 = ArrayAdapter(applicationContext, R.layout.layout_category, itemsIncome)
        viewInflater.toggleMoneyButton.check(R.id.toggleIncome)
        textCategory.setAdapter(categoryAdapter1)
        viewInflater.toggleMoneyButton.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                if (checkedId == R.id.toggleIncome) {
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
            refTrans.child("index").setValue(totalTrans + 1)
            refTrans.child("category").setValue(textCategory.text.toString())
            refTrans.child("currentMonth").setValue((date.get(Calendar.MONTH) + 1).toString())
            refTrans.child("currentYear").setValue(date.get(Calendar.YEAR).toString())
            if (inorout == "true") {
                ref1.child("income").setValue((income?.toLong()?.plus(money.toLong())).toString())
                ref1.child("balance").setValue((balance?.toLong()?.plus(money.toLong())).toString())
            }
            else {
                ref1.child("expense").setValue((expense?.toLong()?.plus(money.toLong())).toString())
                ref1.child("balance").setValue((balance?.toLong()?.minus(money.toLong())).toString())
                val tmp = checkIfHasLimit(textCategory.text.toString())
                if (tmp != 0) {
                    val curruntLimit = limitList[tmp - 1].current!!.toInt()
                    Log.d("khaidff", limitList[tmp - 1].toString())
                    ref1.child("limits").child("limit" + tmp).child("currentLimit").setValue((curruntLimit + money.toInt()).toString())
                }
            }
        }
        builder.setNegativeButton("Cancel"
        ) { dialog, which -> dialog.cancel() }
        builder.show()
    }
    fun checkIfHasLimit(str: String): Int {
        for (data in limitList) {
            if (data.limitedGroup == str) return data.index!!.toInt()
        }
        return 0
    }

    override fun onResume() {
        super.onResume()
        checked = true
    }

    override fun onPause() {
        super.onPause()
        checked = false
    }

    private fun setUI() {
        //hide action bar
        if (supportActionBar != null) {
            supportActionBar!!.hide();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.parseColor("#FFFFFF")
        }
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
        transaction.addToBackStack(null)
        transaction.commit()
        bottomNavigationView.setOnItemSelectedListener(mOnBottomNavigationView)
    }

    private val mOnBottomNavigationView = BottomNavigationView.OnNavigationItemSelectedListener {
        when (it.itemId) {
            R.id.navHome -> {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.container_fragment, HomeFragment())
                transaction.commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navReport -> {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.container_fragment, ReportFragment())
                transaction.commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navPlanning -> {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.container_fragment, PlanningFragment())
                transaction.commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navUser -> {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.container_fragment, UserFragment())
                transaction.commit()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    fun setUpDatabase(data: DataSnapshot?) {
        val db = ref.child(user?.uid.toString())
        val savingDb = db.child("savings")
        val transactionDb = db.child("transactions")
        val cardDb = db.child("cards")
        val limitDb = db.child("limits")
        name = data?.child(user?.uid.toString())!!.child("name").value.toString()
        income = data.child(user?.uid.toString()).child("income").value.toString()
        expense = data.child(user?.uid.toString()).child("expense").value.toString()
        balance = data.child(user?.uid.toString()).child("balance").value.toString()
        getDatabase(savingDb, object : OnGetDataListener {
            override fun onSuccess(dataSnapshot: DataSnapshot) {
                savingList.clear()
                indexSaving = dataSnapshot.child("total").value.toString().toInt()
                for (data in dataSnapshot.children) {
                    if (data.key.toString() != "total") {
                        val index = data.child("index").value.toString()
                        val current = data.child("current").value.toString()
                        val price = data.child("price").value.toString()
                        val product = data.child("product").value.toString()
                        savingList.add(Saving(index.toIntOrNull(), current, null, price, product))
                    }
                }
                savingAdapter.notifyDataSetChanged()
            }
            override fun onStart() {
            }
            override fun onFailure() {
            }
        })
        getDatabase(transactionDb.orderByChild("index"), object : OnGetDataListener {
            override fun onSuccess(dataSnapshot: DataSnapshot) {
                totalTrans = 0
                transactionList.clear()
                for (data in dataSnapshot.children) {
                    val day = data.child("day").value.toString()
                    val originalDay = SimpleDateFormat("yyyy/MM/dd")
                    val targetDay = SimpleDateFormat("dd/MM/yyyy")
                    val tmpDayOriginal = originalDay.parse(day)
                    val tmpDayTarget = targetDay.format(tmpDayOriginal)
                    val inorout = data.child("inorout").value.toString()
                    val money = data.child("money").value.toString()
                    val name = data.child("name").value.toString()
                    val time = data.child("time").value.toString()
                    transactionList.add(RecentTransaction(tmpDayTarget, inorout, money, name, time))
                    totalTrans++
                }
                transactionList.reverse()
                transactionAdapter.notifyDataSetChanged()
            }
            override fun onStart() {
            }
            override fun onFailure() {
            }
        })
        getDatabase(cardDb, object : OnGetDataListener {
            override fun onSuccess(dataSnapshot: DataSnapshot) {
                cardList.clear()
                indexCard = dataSnapshot.child("total").value.toString().toInt()
                for (data in dataSnapshot.children) {
                    if (data.key.toString() != "total") {
                        val index = data.child("index").value.toString()
                        val accNum = data.child("accountNumber").value.toString()
                        val bankName = data.child("bankName").value.toString()
                        val cardNum = data.child("cardNumber").value.toString()
                        val date = data.child("expiredDate").value.toString()
                        val name = data.child("name").value.toString()
                        val namePerson = data.child("namePerson").value.toString()
                        cardList.add(Card(accNum, bankName, cardNum, date, index.toIntOrNull(), name, namePerson))
                    }
                }
                cardAdapter.notifyDataSetChanged()
            }
            override fun onStart() {
            }
            override fun onFailure() {
            }
        })
        getDatabase(limitDb.orderByChild("index"), object : OnGetDataListener {
            override fun onSuccess(dataSnapshot: DataSnapshot) {
                limitList.clear()
                indexLimitation = dataSnapshot.child("total").value.toString().toInt()
                for (data in dataSnapshot.children) {
                    if (data.key.toString() != "total") {
                        val index = data.child("index").value.toString()
                        val nameLimit = data.child("nameLimit").value.toString()
                        val costLimit = data.child("costLimit").value.toString()
                        val currentLimit = data.child("currentLimit").value.toString()
                        limitList.add(Limitation(index.toIntOrNull(), currentLimit, costLimit, nameLimit))
                    }
                }
                limitationAdapter.notifyDataSetChanged()
            }
            override fun onStart() {
            }
            override fun onFailure() {
            }
        })
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
    fun getIndexSaving(): Int {
        return indexSaving
    }
    fun getIndexCard(): Int {
        return indexCard
    }
    fun getIndexLimitation(): Int {
        return indexLimitation
    }
    fun getTransactionAdapter(): RecentTransactionAdapter {
        return transactionAdapter
    }
    fun getCardAdapter(): CardAdapter {
        return cardAdapter
    }
    fun getLimitationAdapter(): LimitationAdapter {
        return limitationAdapter
    }
    fun getSavingAdapter(): SavingAdapter {
        return savingAdapter
    }

}




