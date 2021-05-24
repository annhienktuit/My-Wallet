package com.annhienktuit.mywallet.activity

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import androidx.compose.animation.core.snap
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.annhienktuit.mywallet.R
import com.annhienktuit.mywallet.`object`.*
import com.annhienktuit.mywallet.adapter.SavingDetailAdapter
import com.annhienktuit.mywallet.fragments.PlanningFragment
import com.annhienktuit.mywallet.utils.Extensions.toast
import com.annhienktuit.mywallet.utils.FirebaseUtils
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_saving.*
import java.lang.Math.round
import kotlin.math.roundToInt

class SavingActivity : AppCompatActivity() {
    var savingDetailList = ArrayList<SavingDetail>()
    var async = AppDatabase()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saving)
        var pos = intent.getIntExtra("position", 0)
        async.pos = pos + 1
        async.execute()
        var tempDialog = ProgressDialog(this@SavingActivity)
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
                setData()
            }
        }
        mCountDownTimer.start()

        btn_back_saving.setOnClickListener {
            onBackPressed()
        }
    }

    fun setData() {
        savingDetailList = async.getSavingDetails()
        var recyclerTransactionSaving = findViewById(R.id.recyclerTransactionSaving) as RecyclerView
        recyclerTransactionSaving.adapter = SavingDetailAdapter(savingDetailList)
        recyclerTransactionSaving.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerTransactionSaving.setHasFixedSize(true)
        var nameProduct = findViewById<TextView>(R.id.nameOfSavingProduct)
        var percent = findViewById<TextView>(R.id.percentage)
        var total = findViewById<TextView>(R.id.totalSaving)
        var current = findViewById<TextView>(R.id.currentSaving)
        var progressBar = findViewById<ProgressBar>(R.id.progressSavings)
        nameProduct.text = async.getProduct()
        total.text = "of " + async.getPrice() + " VND"
        current.text = async.getCurrent() + " "
        var tmp1 = async.getCurrent()!!.toInt()
        var tmp2 = async.getPrice()!!.toInt()
        var per = tmp1 * 100 / tmp2
        percent.text = per.toString() + "%"
        progressBar.progress = per
    }

    class AppDatabase() : AsyncTask<Void, Void, Void>() {
        val user: FirebaseUser? = FirebaseUtils.firebaseAuth.currentUser
        private var current: String? = null
        private var product: String? = null
        private var total: String? = null
        var pos: Int = 0
            set(value) {
                field = value
            }
        var db = FirebaseDatabase
            .getInstance("https://my-wallet-80ed7-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("datas").child(user?.uid.toString()).child("savings")
        var savingDetailList = ArrayList<SavingDetail>()
        override fun onPreExecute() {
            db.keepSynced(true)
            Log.d("khaiho", pos.toString())
        }
        override fun doInBackground(vararg params: Void?): Void? {
            try {
                db = db.child("saving" + pos)
                db.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        current = snapshot.child("current").value.toString()
                        total = snapshot.child("price").value.toString()
                        product = snapshot.child("product").value.toString()
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Log.d("khaihoan", "failed")
                    }
                })
                var detailDb = db.child("details")
                detailDb.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (data in snapshot.children) {
                            var model = detailDb.child(data.key.toString())
                            model.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    var tmp1 = snapshot.child("cost").value.toString()
                                    var tmp2 = snapshot.child("day").value.toString()
                                    var tmp3 = snapshot.child("time").value.toString()
                                    var tmp4 = snapshot.child("transName").value.toString()
                                    savingDetailList.add(SavingDetail(tmp1, tmp2, tmp3, tmp4))
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

        fun getSavingDetails(): ArrayList<SavingDetail> {
            return savingDetailList
        }
        fun getCurrent(): String? {
            return current.toString()
        }
        fun getPrice(): String? {
            return total.toString()
        }
        fun getProduct(): String? {
            return product.toString()
        }
    }

}