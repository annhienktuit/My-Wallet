package com.annhienktuit.mywallet.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.annhienktuit.mywallet.R
import kotlinx.android.synthetic.main.activity_card.*

class CardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)
        btn_back_card.setOnClickListener {
            onBackPressed()
        }
    }
}