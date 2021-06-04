package com.annhienktuit.mywallet.activity

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.annhienktuit.mywallet.R
import com.annhienktuit.mywallet.`object`.RecentTransaction
import com.annhienktuit.mywallet.adapter.RecentTransactionAdapter
import com.annhienktuit.mywallet.utils.FirebaseUtils
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_transaction.*
import java.text.SimpleDateFormat
import java.util.*

class TransactionActivity : AppCompatActivity() {

    val user: FirebaseUser? = FirebaseUtils.firebaseAuth.currentUser
    var ref = FirebaseDatabase
        .getInstance("https://my-wallet-80ed7-default-rtdb.asia-southeast1.firebasedatabase.app/")
        .getReference("datas").child(user?.uid.toString())

    val transactionList = ArrayList<RecentTransaction>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction)
        setData()
        val format = SimpleDateFormat("dd/MM/yyyy")
        val currentDay = format.format(Date().time)
        btn_pick_day_transaction.text = currentDay
        getDatabase(ref.child("transactions").orderByChild("day")
            .equalTo(changeDate(btn_pick_day_transaction.text.toString())), object : OnGetDataListener {
            override fun onSuccess(dataSnapshot: DataSnapshot) {
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
                }
                transactionList.reverse()
                setData()
            }
            override fun onStart() {
            }
            override fun onFailure() {
            }
        })

        btn_pick_day_transaction.setOnClickListener {
            val calendar = Calendar.getInstance()
            val yearr = calendar.get(Calendar.YEAR)
            val monthh = calendar.get(Calendar.MONTH)
            val dayy = calendar.get(Calendar.DAY_OF_MONTH)
            transactionList.clear()
            val datePicker = DatePickerDialog(this, object : DatePickerDialog.OnDateSetListener {
                override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                    val date = dayOfMonth.toString() + "/" + (month + 1).toString() + "/" + year.toString()
                    val formatter = SimpleDateFormat("dd/MM/yyyy")
                    val tmp1 = formatter.parse(date)
                    val tmp2 = formatter.format(tmp1)
                    btn_pick_day_transaction.text = tmp2.toString()
                    getDatabase(ref.child("transactions").orderByChild("day")
                        .equalTo(changeDate(btn_pick_day_transaction.text.toString())), object : OnGetDataListener {
                        override fun onSuccess(dataSnapshot: DataSnapshot) {
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
                            }
                            Collections.reverse(transactionList)
                            setData()
                        }
                        override fun onStart() {
                        }
                        override fun onFailure() {
                        }
                    })
                }
            }, yearr, monthh, dayy)
            datePicker.show()
        }
        btn_back_transaction.setOnClickListener {
            onBackPressed()
        }
    }
    private fun setData() {
        recyclerDetailTransaction.removeAllViews()
        recyclerDetailTransaction.adapter = RecentTransactionAdapter(transactionList)
        recyclerDetailTransaction.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerDetailTransaction.setHasFixedSize(true)
    }

    interface OnGetDataListener {
        fun onSuccess(dataSnapshot: DataSnapshot)
        fun onStart()
        fun onFailure()
    }
    fun getDatabase(ref: DatabaseReference, listener: OnGetDataListener?) {
        listener?.onStart()
        ref.addValueEventListener(object : ValueEventListener {
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
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listener?.onSuccess(snapshot)
            }
            override fun onCancelled(error: DatabaseError) {
                listener?.onFailure()
            }
        })
    }
    private fun changeDate(str: String): String {
        val formatter = SimpleDateFormat("yyyy/MM/dd")
        val formatter1 = SimpleDateFormat("dd/MM/yyyy")
        val tmp1 = formatter1.parse(str)
        return formatter.format(tmp1)
    }
}