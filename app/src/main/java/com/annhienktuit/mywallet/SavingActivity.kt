package com.annhienktuit.mywallet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.annhienktuit.mywallet.`object`.SavingDetail
import com.annhienktuit.mywallet.adapter.SavingDetailAdapter
import com.annhienktuit.mywallet.adapter.WalletAdapter
import kotlinx.android.synthetic.main.activity_saving.*

class SavingActivity : AppCompatActivity() {
    var savingDetailList = ArrayList<SavingDetail>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saving)
        addSavingDetail()
        var recyclerTransactionSaving = findViewById(R.id.recyclerTransactionSaving) as RecyclerView
        recyclerTransactionSaving.adapter = SavingDetailAdapter(savingDetailList)
        recyclerTransactionSaving.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerTransactionSaving.setHasFixedSize(true)
        btn_back_saving.setOnClickListener {
            onBackPressed()
        }
    }
    fun addSavingDetail() {
        savingDetailList.add(SavingDetail("Top Up", "Today - 9:18 AM","60,000"))
        savingDetailList.add(SavingDetail("adsf", "Today - 9:18 AM","60,000"))
        savingDetailList.add(SavingDetail("Tádfafp", "Today - 9:18 AM","60,000"))
        savingDetailList.add(SavingDetail("Tofasdfadfp", "Today - 9:18 AM","60,000"))
        savingDetailList.add(SavingDetail("Tădfasd", "Today - 9:18 AM","60,000"))
        savingDetailList.add(SavingDetail("Top Up", "Today - 9:18 AM","60,000"))
    }
}