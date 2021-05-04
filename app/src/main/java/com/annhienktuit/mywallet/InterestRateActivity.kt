package com.annhienktuit.mywallet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class InterestRateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interest_rate)

        if (supportActionBar != null) {
            supportActionBar?.hide();
        }
    }
}