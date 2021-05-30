package com.annhienktuit.mywallet.activity


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.annhienktuit.mywallet.R
import com.annhienktuit.mywallet.`object`.*
import com.annhienktuit.mywallet.adapter.SavingDetailAdapter
import com.annhienktuit.mywallet.utils.FirebaseUtils
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_saving.*

class SavingActivity : AppCompatActivity() {
    var savingDetailList = ArrayList<SavingDetail>()
    val user: FirebaseUser? = FirebaseUtils.firebaseAuth.currentUser
    var ref = FirebaseDatabase
        .getInstance("https://my-wallet-80ed7-default-rtdb.asia-southeast1.firebasedatabase.app/")
        .getReference("datas").child(user?.uid.toString()).child("savings")
    var saving: Saving? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saving)
        ref.keepSynced(true)
        var pos = intent.getIntExtra("position", 0)
        getDatabase(ref.child("saving" + (pos + 1)).child("details"), object : OnGetDataListener {
            override fun onSuccess(dataSnapshot: DataSnapshot) {
                for (data in dataSnapshot.children) {
                    var tmp1 = data.child("cost").value.toString()
                    var tmp2 = data.child("day").value.toString()
                    var tmp3 = data.child("time").value.toString()
                    var tmp4 = data.child("transName").value.toString()
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
    }

    fun setData(saving: Saving?) {
        var recyclerTransactionSaving = findViewById(R.id.recyclerTransactionSaving) as RecyclerView
        recyclerTransactionSaving.adapter = SavingDetailAdapter(savingDetailList)
        recyclerTransactionSaving.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerTransactionSaving.setHasFixedSize(true)

        nameOfSavingProduct.text = saving?.nameOfProduct.toString()
        totalSaving.text = "of " + saving?.moneyOfProduct.toString() + " VND"
        currentSaving.text = saving?.currentSaving.toString() + " "
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
    fun getDatabase(ref: DatabaseReference?, listener: OnGetDataListener?) {
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