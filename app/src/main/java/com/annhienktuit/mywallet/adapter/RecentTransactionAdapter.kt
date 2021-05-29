package com.annhienktuit.mywallet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.annhienktuit.mywallet.R
import com.annhienktuit.mywallet.`object`.RecentTransaction
import kotlinx.android.synthetic.main.layout_recent_transaction_1.view.*
import kotlinx.android.synthetic.main.layout_recent_transaction_2.view.*
import java.text.DecimalFormat
import java.text.NumberFormat

class RecentTransactionAdapter(private val transactionList: List<RecentTransaction>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class TransactionViewHolder1(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name1: TextView = itemView.txtNameTransaction1
        val date1: TextView = itemView.txtDateAndName1
        val money1: TextView = itemView.txtMoneyChange1
    }
    class TransactionViewHolder2(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name2: TextView = itemView.txtNameTransaction2
        val date2: TextView = itemView.txtDateAndName2
        val money2: TextView = itemView.txtMoneyChange2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 0) {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_recent_transaction_1, parent, false)
            return TransactionViewHolder1(itemView)
        }
        else{
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_recent_transaction_2, parent, false)
            return TransactionViewHolder2(itemView)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = transactionList[position]
        if (currentItem.inOrOut == "false") {
            var holder = holder as TransactionViewHolder1
            holder.name1.text = currentItem.nameOfTrans
            holder.date1.text = (currentItem.dayOfTrans + " - " + currentItem.timeOfTrans)
            holder.money1.text = "-" + changeToMoney(currentItem.moneyOfTrans)
        } else {
            var holder = holder as TransactionViewHolder2
            holder.name2.text = currentItem.nameOfTrans
            holder.date2.text = (currentItem.dayOfTrans + " - " + currentItem.timeOfTrans)
            holder.money2.text = "+" + changeToMoney(currentItem.moneyOfTrans)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (transactionList.get(position).inOrOut == "false")
            return 0
        else
            return 1
    }

    override fun getItemCount() = transactionList.size
    fun changeToMoney(str: String?): String {
        val formatter: NumberFormat = DecimalFormat("#,###")
        val myNumber = str?.toLong()
        val formattedNumber = formatter.format(myNumber)
        return formattedNumber
    }
}
