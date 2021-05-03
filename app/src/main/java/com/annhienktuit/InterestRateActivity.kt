package com.annhienktuit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.annhienktuit.mywallet.R

class InterestRateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interest_rate)

        if (supportActionBar != null) {
            supportActionBar?.hide();
        }
    }
}