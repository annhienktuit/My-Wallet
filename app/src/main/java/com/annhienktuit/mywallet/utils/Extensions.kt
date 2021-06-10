package com.annhienktuit.mywallet.utils

import android.app.Activity
import android.widget.Toast
import java.text.DecimalFormat
import java.text.NumberFormat

object Extensions {
    fun Activity.toast(msg: String){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
    fun changeToMoney(str: String?): String {
        val formatter: NumberFormat = DecimalFormat("#,###")
        if (str != null) {
            val myNumber = str.toDouble()
            if (myNumber < 0)
                return "-" + formatter.format(-myNumber)
            else
                return formatter.format(myNumber)
        }
        return "0"
    }

}