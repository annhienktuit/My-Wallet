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
        if (position % 2 == 0) {
            var holder = holder as TransactionViewHolder1
            holder.name1.text = currentItem.nameOfTrans
            holder.date1.text = (currentItem.dayOfTrans + " - " + currentItem.timeOfTrans)
            holder.money1.text = currentItem.moneyOfTrans
        } else {
            var holder = holder as TransactionViewHolder2
            holder.name2.text = currentItem.nameOfTrans
            holder.date2.text = (currentItem.dayOfTrans + " - " + currentItem.timeOfTrans)
            holder.money2.text = currentItem.moneyOfTrans
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position % 2
    }

    override fun getItemCount() = transactionList.size
}
