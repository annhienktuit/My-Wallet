package com.annhienktuit.mywallet.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.annhienktuit.mywallet.R

class AllMonthReport : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_month_report)

        if (supportActionBar != null) {
            supportActionBar!!.hide();
        }


    }
}