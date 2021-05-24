package com.annhienktuit.mywallet.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.annhienktuit.mywallet.*
import com.annhienktuit.mywallet.dialog.DoneInterestDialog
import com.annhienktuit.mywallet.dialog.PeriodOfTimeDialog
import com.annhienktuit.mywallet.dialog.TypeOfInterestDialog
import com.annhienktuit.mywallet.dialog.TypeOfTimeDialog
import kotlinx.android.synthetic.main.activity_interest_rate.*

class InterestRateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interest_rate)

        if (supportActionBar != null) {
            supportActionBar?.hide();
        }

        btnArrowBack.setOnClickListener{
            finish()
        }

        TypeOfInterestLayout.setOnClickListener{
            var dialog = TypeOfInterestDialog()

            dialog.show(supportFragmentManager, "TypeOfInterestDialog")
        }

        periodOfTimeLayout.setOnClickListener{
            var dialog = PeriodOfTimeDialog()

            dialog.show(supportFragmentManager, "PeriodOfTimeDialog")
        }

        typeOfTimeLayout.setOnClickListener{
            var dialog = TypeOfTimeDialog()

            dialog.show(supportFragmentManager, "TypeOfTimeDialog")
        }

        doneBtn.setOnClickListener{
            var dialog = DoneInterestDialog()

            dialog.show(supportFragmentManager, "DoneDialog")
        }
    }
}