package com.annhienktuit.mywallet.activity

import android.R.attr.label
import android.app.ProgressDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.AsyncTask
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.annhienktuit.mywallet.R
import com.annhienktuit.mywallet.utils.FirebaseUtils
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_card.*


class CardActivity : AppCompatActivity() {
    var async = AppDatabase()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)
        var pos = intent.getIntExtra("position", 0)
        async.pos = pos + 1
        async.execute()
        var tempDialog = ProgressDialog(this@CardActivity)
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
        btn_back_card.setOnClickListener {
            onBackPressed()
        }
        btnCopyName.setOnClickListener {
            val clipboard: ClipboardManager =
                getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Copied", async.getPerson().toString())
            clipboard.setPrimaryClip(clip)
        }
        btnCopyAccountNumber.setOnClickListener {
            val clipboard: ClipboardManager =
                getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Copied", async.getAccount().toString())
            clipboard.setPrimaryClip(clip)
        }
        btnCopyCardNumber.setOnClickListener {
            val clipboard: ClipboardManager =
                getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Copied", async.getCardNum().toString())
            clipboard.setPrimaryClip(clip)
        }
    }
    fun setData() {
        numberCard.text = async.getCardNum().toString()
        dateValid.text = async.getDate().toString()
        nameCard.text = async.getPerson().toString()
        NameContent.text = async.getPerson().toString()
        NoAccount.text = async.getAccount().toString()
        NoCard.text = async.getCardNum().toString()
    }
    class AppDatabase() : AsyncTask<Void, Void, Void>() {
        val user: FirebaseUser? = FirebaseUtils.firebaseAuth.currentUser
        private var accountNum: String? = null
        private var bankName: String? = null
        private var cardNum: String? = null
        private var expiredDate: String? = null
        private var nameOnCard: String? = null
        private var personName: String? = null
        var pos: Int = 0
            set(value) {
                field = value
            }
        var db = FirebaseDatabase
            .getInstance("https://my-wallet-80ed7-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("datas").child(user?.uid.toString()).child("cards")
        override fun onPreExecute() {
            db.keepSynced(true)
        }
        override fun doInBackground(vararg params: Void?): Void? {
            try {
                db = db.child("card" + pos)
                db.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        accountNum = snapshot.child("accountNumber").value.toString()
                        bankName = snapshot.child("bankName").value.toString()
                        cardNum = snapshot.child("cardNumber").value.toString()
                        expiredDate = snapshot.child("expiredDate").value.toString()
                        nameOnCard = snapshot.child("name").value.toString()
                        personName = snapshot.child("namePerson").value.toString()
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

        fun getAccount(): String? {
            return accountNum.toString()
        }
        fun getBank(): String? {
            return bankName.toString()
        }
        fun getCardNum(): String? {
            return cardNum.toString()
        }
        fun getDate(): String? {
            return expiredDate.toString()
        }
        fun getNameCard(): String? {
            return nameOnCard.toString()
        }
        fun getPerson(): String? {
            return personName.toString()
        }
    }
}