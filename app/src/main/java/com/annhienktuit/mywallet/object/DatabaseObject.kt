package com.annhienktuit.mywallet.`object`

data class DatabaseObject(
    val balance: String? = null,
    val cards: ArrayList<Card>? = null,
    val dob: String? = null,
    val expense: String? = null,
    val income: String? = null,
    val name: String? = null,
    val phone: String? = null,
    val savings: ArrayList<Saving>? = null,
    val transactions: ArrayList<RecentTransaction>? = null,
)