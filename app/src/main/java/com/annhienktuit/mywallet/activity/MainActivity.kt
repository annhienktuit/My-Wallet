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
import com.anychart.APIlib
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Pie
import com.anychart.enums.Align
import com.anychart.enums.LegendLayout
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

//GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
//if (acct != null) {
//    String personName = acct.getDisplayName();
//    String personEmail = acct.getEmail();
//    String personId = acct.getId();
//}
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
    //-----------------------------------
    //Variables for current income reporting
    private var listCurrentIncome = mutableListOf<DetailTransaction>()
    private var amountCurrentIncome = 0L
    private var amountCurrentDebt = 0L
    private var listCurrentIncomeData = mutableListOf<DataEntry>()
    //-----------------------------------
    //Variables for current expense reporting
    private var listCurrentExpense = mutableListOf<DetailTransaction>()
    private var amountCurrentExpense = 0L
    private var amountCurrentLoan = 0L
    private var listCurrentExpenseData = mutableListOf<DataEntry>()
    //-----------------------------------
    //Variables for Previous income reporting
    private var listPreviousIncome = mutableListOf<DetailTransaction>()
    private var amountPreviousIncome = 0L
    private var amountPreviousDebt = 0L
    private var listPreviousIncomeData = mutableListOf<DataEntry>()
    //-----------------------------------
    //Variables for Previous expense reporting
    private var listPreviousExpense = mutableListOf<DetailTransaction>()
    private var amountPreviousExpense = 0L
    private var amountPreviousLoan = 0L
    private var listPreviousExpenseData = mutableListOf<DataEntry>()
    //-----------------------------------
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
                        val fullName = intent.getStringExtra("fullname").toString()
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
                setCurrentIncomePieChartData()
                setCurrentExpensePieChartData()

                setPreviousIncomePieChartData()
                setPreviousExpensePieChartData()
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

    fun setCurrentIncomePieChartData() {
        //reference of Income
        val refIncome = ref.child(user?.uid.toString()).child("transactions").orderByChild("inorout").equalTo("true")

        //get current month
        var now: Calendar = Calendar.getInstance()
        var currentMonth = now.get(Calendar.MONTH) + 1

        refIncome.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                amountCurrentIncome = 0L
                amountCurrentDebt = 0L

                listCurrentIncome.removeAll(listCurrentIncome)
                listCurrentIncomeData.removeAll(listCurrentIncomeData)

                for(childBranch in snapshot.children){
                    listCurrentIncome.add(DetailTransaction(
                        childBranch.child("category").value.toString(),
                        childBranch.child("money").value.toString(),
                        childBranch.child("currentMonth").value.toString()
                    ))
                }

                listCurrentIncome = handleListForChart(listCurrentIncome, currentMonth)

                for(item in listCurrentIncome){
                    if(item.category == "Debt"){
                        amountCurrentDebt += item.moneyAmount.toLong()
                    }
                    listCurrentIncomeData.add(ValueDataEntry(item.category, item.moneyAmount.toLong()))
                    amountCurrentIncome += item.moneyAmount.toLong()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

    }

    fun setCurrentExpensePieChartData() {
        //reference of expense
        val refExpense = ref.child(user?.uid.toString()).child("transactions").orderByChild("inorout").equalTo("false")

        //get current month
        var now: Calendar = Calendar.getInstance()
        var currentMonth = now.get(Calendar.MONTH) + 1

        refExpense.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                amountCurrentExpense = 0L
                amountCurrentLoan = 0L

                listCurrentExpense.removeAll(listCurrentExpense)
                listCurrentExpenseData.removeAll(listCurrentExpenseData)

                for(childBranch in snapshot.children){
                    listCurrentExpense.add(DetailTransaction(
                        childBranch.child("category").value.toString(),
                        childBranch.child("money").value.toString(),
                        childBranch.child("currentMonth").value.toString()
                    ))
                }

                listCurrentExpense = handleListForChart(listCurrentExpense, currentMonth)

                for(item in listCurrentExpense){
                    if(item.category == "Loan"){
                        amountCurrentLoan += item.moneyAmount.toLong()
                    }
                    listCurrentExpenseData.add(ValueDataEntry(item.category, item.moneyAmount.toLong()))
                    amountCurrentExpense += item.moneyAmount.toLong()
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

    }

    fun setPreviousIncomePieChartData() {
        //reference of Income
        val refIncome = ref.child(user?.uid.toString()).child("transactions").orderByChild("inorout").equalTo("true")

        //get previous month
        var now: Calendar = Calendar.getInstance()
        var previousMonth = now.get(Calendar.MONTH)

        if(previousMonth == 0){
            previousMonth = 12
        }

        refIncome.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                amountPreviousIncome = 0L
                amountPreviousDebt = 0L

                listPreviousIncome.removeAll(listPreviousIncome)
                listPreviousIncomeData.removeAll(listPreviousIncomeData)
                for(childBranch in snapshot.children){
                    listPreviousIncome.add(DetailTransaction(
                        childBranch.child("category").value.toString(),
                        childBranch.child("money").value.toString(),
                        childBranch.child("currentMonth").value.toString()
                    ))
                }

                listPreviousIncome = handleListForChart(listPreviousIncome, previousMonth)

                for(item in listPreviousIncome){
                    if(item.category == "Debt"){
                        amountPreviousDebt += item.moneyAmount.toLong()
                    }
                    listPreviousIncomeData.add(ValueDataEntry(item.category, item.moneyAmount.toLong()))
                    amountPreviousIncome += item.moneyAmount.toLong()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

    }

    fun setPreviousExpensePieChartData() {
        //reference of expense
        val refExpense = ref.child(user?.uid.toString()).child("transactions").orderByChild("inorout").equalTo("false")

        //get previous month
        var now: Calendar = Calendar.getInstance()
        var previousMonth = now.get(Calendar.MONTH)

        if(previousMonth == 0){
            previousMonth = 12
        }

        refExpense.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                amountPreviousExpense = 0L
                amountPreviousLoan = 0L

                listPreviousExpenseData.removeAll(listPreviousExpenseData)
                listPreviousExpense.removeAll(listPreviousExpense)
                for(childBranch in snapshot.children){
                    listPreviousExpense.add(DetailTransaction(
                        childBranch.child("category").value.toString(),
                        childBranch.child("money").value.toString(),
                        childBranch.child("currentMonth").value.toString()
                    ))
                }

                listPreviousExpense = handleListForChart(listPreviousExpense, previousMonth)

                for(item in listPreviousExpense){
                    if(item.category == "Loan"){
                        amountPreviousLoan += item.moneyAmount.toLong()
                    }
                    listPreviousExpenseData.add(ValueDataEntry(item.category, item.moneyAmount.toLong()))
                    amountPreviousExpense += item.moneyAmount.toLong()
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

    }


    private fun handleListForChart(list: MutableList<DetailTransaction>, month: Int) : MutableList<DetailTransaction>{
        var now: Calendar = Calendar.getInstance()
        var currentYear = now.get(Calendar.YEAR)

        var iForCurrentMonth = 0
        while(iForCurrentMonth < list.size){
            if(list[iForCurrentMonth].currentMonth != month.toString() || list[iForCurrentMonth].currentYear != currentYear.toString()){
                list.remove(list[iForCurrentMonth])
                iForCurrentMonth--
            }
            iForCurrentMonth++
        }

        var i = 0;
        while(i < list.size){
            var j = i + 1
            while(j < list.size){
                if(list[j].category == list[i].category){
                    var moneyTemp: Long = list[i].moneyAmount.toLong()
                    moneyTemp += list[j].moneyAmount.toLong()
                    list[i].moneyAmount = moneyTemp.toString()
                    list.remove(list[j])
                    j--
                }
                j++
            }
            i++
        }

        return list
    }

    fun getCurrentIncome(): Long{
        return amountCurrentIncome
    }

    fun getCurrentDebt() : Long{
        return amountCurrentDebt
    }

    fun getCurrentIncomeList() : MutableList<DetailTransaction>{
        return listCurrentIncome
    }

    fun getCurrentIncomeData() : MutableList<DataEntry>{
        return listCurrentIncomeData
    }

    fun getCurrentExpense(): Long{
        return amountCurrentExpense
    }

    fun getCurrentLoan() : Long{
        return amountCurrentLoan
    }

    fun getCurrentExpenseList() : MutableList<DetailTransaction>{
        return listCurrentExpense
    }

    fun getCurrentExpenseData() : MutableList<DataEntry>{
        return listCurrentExpenseData
    }

    //--------------------------
    fun getPreviousIncome(): Long{
        return amountPreviousIncome
    }

    fun getPreviousDebt() : Long{
        return amountPreviousDebt
    }

    fun getPreviousIncomeList() : MutableList<DetailTransaction>{
        return listPreviousIncome
    }

    fun getPreviousIncomeData() : MutableList<DataEntry>{
        return listPreviousIncomeData
    }

    fun getPreviousExpense(): Long{
        return amountPreviousExpense
    }

    fun getPreviousLoan() : Long{
        return amountPreviousLoan
    }

    fun getPreviousExpenseList() : MutableList<DetailTransaction>{
        return listPreviousExpense
    }

    fun getPreviousExpenseData() : MutableList<DataEntry>{
        return listPreviousExpenseData
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




