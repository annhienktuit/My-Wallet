package com.annhienktuit.mywallet.activity


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
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
    var saving: Saving? = null
    var totalDetail: Int = 0
    var pos: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saving)
        ref.keepSynced(true)
        pos = intent.getIntExtra("position", 0)
        getDatabase(ref.child("saving" + (pos + 1)).child("details"), object : OnGetDataListener {
            override fun onSuccess(dataSnapshot: DataSnapshot) {
                totalDetail = 0
                savingDetailList.clear()
                for (data in dataSnapshot.children) {
                    var tmp1 = data.child("cost").value.toString()
                    var tmp2 = data.child("day").value.toString()
                    var tmp3 = data.child("time").value.toString()
                    var tmp4 = data.child("transName").value.toString()
                    totalDetail++;
                    savingDetailList.add(SavingDetail(tmp1, tmp2, tmp3, tmp4))
                }
            }

            override fun onStart() {

            }

            override fun onFailure() {

            }
        })
        getDatabase(ref.child("saving" + (pos + 1)), object : OnGetDataListener {
            override fun onSuccess(dataSnapshot: DataSnapshot) {
                var tmp1 = dataSnapshot.child("current").value.toString()
                var tmp2 = dataSnapshot.child("price").value.toString()
                var tmp3 = dataSnapshot.child("product").value.toString()
                saving = Saving(tmp1, savingDetailList, tmp2, tmp3)
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
            val money = editMoney.text.toString()
            var date = Calendar.getInstance()
            var dayFormatter = SimpleDateFormat("dd/MM/yyyy")
            var timeFormatter = SimpleDateFormat("hh:mm")
            var day = dayFormatter.format(date.time)
            var time = timeFormatter.format(date.time)
            val ref3 = ref.child("saving" + (pos + 1))
            val ref2 = ref.child("saving" + (pos + 1)).child("details").child("detail" + (totalDetail + 1))
            ref2.child("cost").setValue(money)
            ref2.child("day").setValue(day)
            ref2.child("time").setValue(time)
            ref2.child("transName").setValue(name)
            ref3.child("current").setValue((saving!!.currentSaving!!.toLong() + money.toLong()).toString())
        }
        builder.setNegativeButton("Cancel"
        ) { dialog, which -> dialog.cancel() }
        builder.show()
    }

    private fun setData(saving: Saving?) {
        var recyclerTransactionSaving = findViewById(R.id.recyclerTransactionSaving) as RecyclerView
        recyclerTransactionSaving.adapter = SavingDetailAdapter(savingDetailList)
        recyclerTransactionSaving.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerTransactionSaving.setHasFixedSize(true)

        nameOfSavingProduct.text = saving?.nameOfProduct.toString()
        if (saving?.moneyOfProduct.toString() != null && saving?.currentSaving.toString() != null) {
            totalSaving.text = "of " + changeToMoney(saving?.moneyOfProduct.toString()) + " VND"
            currentSaving.text = changeToMoney(saving?.currentSaving.toString()) + " "
        }
        var tmp1 = saving!!.currentSaving!!.toInt()
        var tmp2 = saving!!.moneyOfProduct!!.toInt()
        var per = tmp1 * 100 / tmp2
        percentage.text = per.toString() + "%"
        progressSavings.progress = per
    }
    interface OnGetDataListener {
        fun onSuccess(dataSnapshot: DataSnapshot)
        fun onStart()
        fun onFailure()
    }
    private fun getDatabase(ref: DatabaseReference?, listener: OnGetDataListener?) {
        listener?.onStart()
        ref?.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                listener?.onSuccess(snapshot)
            }
            override fun onCancelled(error: DatabaseError) {
                listener?.onFailure()
            }
        })
    }


}