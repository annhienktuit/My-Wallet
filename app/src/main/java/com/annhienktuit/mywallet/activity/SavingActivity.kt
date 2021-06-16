package com.annhienktuit.mywallet.activity


import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.annhienktuit.mywallet.R
import com.annhienktuit.mywallet.`object`.*
import com.annhienktuit.mywallet.adapter.SavingDetailAdapter
import com.annhienktuit.mywallet.utils.Extensions.changeToMoney
import com.annhienktuit.mywallet.utils.Extensions.toast
import com.annhienktuit.mywallet.utils.FirebaseUtils
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_saving.*
import kotlinx.android.synthetic.main.dialog_add_saving_detail.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SavingActivity : AppCompatActivity() {
    var savingDetailList = ArrayList<SavingDetail>()
    val user: FirebaseUser? = FirebaseUtils.firebaseAuth.currentUser
    var ref = FirebaseDatabase
        .getInstance("https://my-wallet-80ed7-default-rtdb.asia-southeast1.firebasedatabase.app/")
        .getReference("datas").child(user?.uid.toString()).child("savings")
    val ref1 = FirebaseDatabase
        .getInstance("https://my-wallet-80ed7-default-rtdb.asia-southeast1.firebasedatabase.app/")
        .getReference("datas").child(user?.uid.toString())
    var refTrans = ref1.child("transactions")
    //-----------------------------------------------
    var saving: Saving? = null
    var totalDetail: Int = 0
    //---------------------------------------------
    var pos: Int = 0
    var current: String? = null
    var total: String? = null
    var nameProduct: String? = null
    //---------------------------------------------
    var expense: String? = null
    var balance: String? = null
    var totalTrans: Int = 0
    var left: Long = 0
    //---------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saving)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        ref.keepSynced(true)
        pos = intent.getIntExtra("position", 0)
        //Get total expense
        getDatabase(ref1, object : OnGetDataListener {
            override fun onSuccess(dataSnapshot: DataSnapshot) {
                expense = dataSnapshot.child("expense").value.toString()
                balance = dataSnapshot.child("balance").value.toString()
            }
            override fun onStart() {
            }
            override fun onFailure() {
            }
        })
        //Get total transaction
        getDatabase(ref1.child("transactions"), object : OnGetDataListener {
            override fun onSuccess(dataSnapshot: DataSnapshot) {
                totalTrans = 0
                for (data in dataSnapshot.children) {
                    totalTrans++
                }
                refTrans = ref1.child("transactions").child("transaction" + (totalTrans + 1))
            }
            override fun onStart() {
            }
            override fun onFailure() {
            }
        })
        //Get detail transaction
        getDatabase(ref.child("saving" + pos).child("details").orderByChild("day"), object : OnGetDataListener {
            override fun onSuccess(dataSnapshot: DataSnapshot) {
                totalDetail = 0
                savingDetailList.clear()
                for (data in dataSnapshot.children) {
                    var tmp1 = data.child("cost").value.toString()
                    var tmp2 = data.child("day").value.toString()
                    var tmp3 = data.child("time").value.toString()
                    var tmp4 = data.child("transName").value.toString()
                    try {
                        val originalDay = SimpleDateFormat("yyyy/MM/dd")
                        val targetDay = SimpleDateFormat("dd/MM/yyyy")
                        val tmpDayOriginal = originalDay.parse(tmp2)
                        val tmpDayTarget = targetDay.format(tmpDayOriginal)
                        savingDetailList.add(SavingDetail(tmp1, tmpDayTarget, tmp3, tmp4))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    totalDetail++;

                }
                savingDetailList.reverse()
            }

            override fun onStart() {

            }

            override fun onFailure() {

            }
        })
        getDatabase(ref.child("saving" + pos), object : OnGetDataListener {
            override fun onSuccess(dataSnapshot: DataSnapshot) {
                val index = dataSnapshot.child("index").value.toString()
                val tmp1 = dataSnapshot.child("current").value.toString()
                val tmp2 = dataSnapshot.child("price").value.toString()
                val tmp3 = dataSnapshot.child("product").value.toString()
                current = tmp1
                total = tmp2
                nameProduct = tmp3
                left = tmp2.toLong() - tmp1.toLong()
                saving = Saving(index.toIntOrNull(), tmp1, savingDetailList, tmp2, tmp3)
                setData(saving)
            }

            override fun onStart() {

            }

            override fun onFailure() {

            }
        })
        btn_back_saving.setOnClickListener {
            finish()
        }
        floatingAdd.setOnClickListener {
            eventOnClickAddButton()
        }
    }

    private fun eventOnClickAddButton() {
        val builder = AlertDialog.Builder(this)
        val viewInflater = LayoutInflater.from(this).inflate(R.layout.dialog_add_saving_detail, null, false)
        val editName = viewInflater.findViewById<EditText>(R.id.editNameSaving)
        val editMoney = viewInflater.findViewById<EditText>(R.id.editMoneySaving)
        builder.setView(viewInflater)
        builder.setPositiveButton("OK"
        ) { dialog, which ->
            val name = editName.text.toString()
            var money = editMoney.text.toString()
            if (money.toLong() > left) money = left.toString()
            var date = Calendar.getInstance()
            var dayFormatter = SimpleDateFormat("yyyy/MM/dd")
            var timeFormatter = SimpleDateFormat("HH:mm")
            var day = dayFormatter.format(date.time)
            var time = timeFormatter.format(date.time)
            val ref3 = ref.child("saving" + pos)
            val ref2 = ref.child("saving" + pos).child("details").child("detail" + (totalDetail + 1))
            //Set data in main activity recent transaction
            ref1.child("expense").setValue((expense?.toLong()?.plus(money.toLong())).toString())
            ref1.child("balance").setValue((balance?.toLong()?.minus(money.toLong())).toString())
            refTrans.child("day").setValue(day)
            refTrans.child("money").setValue(money)
            refTrans.child("name").setValue("Saving for " + nameProduct)
            refTrans.child("time").setValue(time)
            refTrans.child("inorout").setValue("false")
            refTrans.child("index").setValue(totalTrans + 1)
            refTrans.child("category").setValue("Saving")
            refTrans.child("currentMonth").setValue((date.get(Calendar.MONTH) + 1).toString())
            refTrans.child("currentYear").setValue(date.get(Calendar.YEAR).toString())
            //Set data in saving transaction
            ref2.child("cost").setValue(money)
            ref2.child("day").setValue(day)
            ref2.child("time").setValue(time)
            ref2.child("transName").setValue(name)
            ref3.child("current").setValue((saving!!.currentSaving!!.toLong() + money.toLong()).toString())
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel"
        ) { dialog, which -> dialog.dismiss() }
        builder.create()
        builder.show()
    }

    private fun setData(saving: Saving?) {
        recyclerTransactionSaving.adapter = SavingDetailAdapter(savingDetailList)
        recyclerTransactionSaving.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerTransactionSaving.setHasFixedSize(true)
        nameOfSavingProduct.text = saving?.nameOfProduct.toString()
        if (total != null && current != null) {
            totalSaving.text = "of " + changeToMoney(total) + " VND"
            currentSaving.text = changeToMoney(current) + " "
        }
        val tmp1 = current?.toInt()
        val tmp2 = total?.toInt()
        if (tmp1 == tmp2) {
            notifyCompleted.text = "You have enough money to buy this product"
            floatingAdd.isEnabled = false
        } else {
            notifyCompleted.text = "You need to save " + changeToMoney((tmp2!! - tmp1!!).toString()) + " VND"
            floatingAdd.isEnabled = true
        }
        val per = tmp1!! * 100 / tmp2!!
        percentage.text = per.toString() + "%"
        progressSavings.progress = per
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


}
