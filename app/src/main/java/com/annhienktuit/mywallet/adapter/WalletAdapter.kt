package com.annhienktuit.mywallet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.annhienktuit.mywallet.R.*
import com.annhienktuit.mywallet.`object`.Wallet
import kotlinx.android.synthetic.main.layout_wallet_item_2.view.*
import kotlinx.android.synthetic.main.layout_wallet_item_1.view.*
import java.text.DecimalFormat
import java.text.NumberFormat

class WalletAdapter(private val walletList: List<Wallet>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class WalletViewHolder1(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name1: TextView = itemView.nameWallet1
        val money1: TextView = itemView.moneyWallet1
    }
    class WalletViewHolder2(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name2: TextView = itemView.nameWallet2
        val money2: TextView = itemView.moneyWallet2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 0) {
            val itemView = LayoutInflater.from(parent.context).inflate(layout.layout_wallet_item_1, parent, false)
            return WalletViewHolder1(itemView)
        }
        else{
            val itemView = LayoutInflater.from(parent.context).inflate(layout.layout_wallet_item_2, parent, false)
            return WalletViewHolder2(itemView)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = walletList[position]
        if (position % 2 == 0) {
            var holder = holder as WalletViewHolder1
            holder.name1.text = currentItem.nameOfWallet
            holder.money1.text = changeToMoney(currentItem.moneyOfWallet)
        } else {
            var holder = holder as WalletViewHolder2
            holder.name2.text = currentItem.nameOfWallet
            holder.money2.text = changeToMoney(currentItem.moneyOfWallet)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position % 2
    }

    override fun getItemCount() = walletList.size
    fun changeToMoney(str: String?): String {
        val formatter: NumberFormat = DecimalFormat("#,###")
        val myNumber = str?.toLong()
        val formattedNumber = formatter.format(myNumber)
        return formattedNumber
    }
}

