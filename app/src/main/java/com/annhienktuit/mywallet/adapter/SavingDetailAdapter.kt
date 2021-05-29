package com.annhienktuit.mywallet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.annhienktuit.mywallet.R
import com.annhienktuit.mywallet.`object`.SavingDetail
import kotlinx.android.synthetic.main.activity_saving.view.*
import kotlinx.android.synthetic.main.layout_saving_transaction.view.*
import java.text.DecimalFormat
import java.text.NumberFormat


class SavingDetailAdapter(private val savingDetailList: List<SavingDetail>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class SavingDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.kindOfSaving
        val date: TextView = itemView.dateOfSaving
        val money: TextView = itemView.moneyOfSaving
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_saving_transaction, parent, false)
        return SavingDetailViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = savingDetailList[position]
        var holder = holder as SavingDetailViewHolder
        holder.name.text = currentItem.nameOfSaving
        holder.money.text = "+ " + changeToMoney(currentItem.costOfSaving)
        holder.date.text = currentItem.dayOfSaving + " - " + currentItem.timeOfSaving
    }

    override fun getItemCount() = savingDetailList.size
    fun changeToMoney(str: String?): String {
        val formatter: NumberFormat = DecimalFormat("#,###")
        val myNumber = str?.toLong()
        val formattedNumber = formatter.format(myNumber)
        return formattedNumber
    }
}