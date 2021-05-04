package com.annhienktuit.mywallet.utils

import kotlin.math.pow
//https://timo.vn/blogs/tai-khoan-tiet-kiem/cach-tinh-lai-suat-ngan-hang-lai-suat-tiet-kiem-nhanh-chong-4/
object CompountInterest {
    fun calculateCompoundInterest(PV: Double, interest: Double, period: Double ): Double {
        return PV*((1+interest).pow(period)) //FV = PV x (1 + i)^n
        //period tinh theo don vi nam
    }
}