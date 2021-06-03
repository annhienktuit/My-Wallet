package com.annhienktuit.mywallet.adapter

import android.util.Log
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
        var holder1 = holder as SavingDetailViewHolder
        holder1.name.text = currentItem.nameOfSaving
        if (currentItem.costOfSaving != null)
            holder1.money.text = "+" + changeToMoney(currentItem.costOfSaving)
        else
            holder1.money.text = "+" + currentItem.costOfSaving
        holder1.date.text = currentItem.dayOfSaving + " - " + currentItem.timeOfSaving
    }

    override fun getItemCount() = savingDetailList.size
    private fun changeToMoney(str: String?): String? {
        val formatter: NumberFormat = DecimalFormat("#,###")
        if (str != null) {
            try {
                val myNumber = str.toDouble()
                if (myNumber != null) {
                    return if (myNumber < 0)
                        formatter.format(-myNumber)
                    else
                        formatter.format(myNumber)
                }
            }
            catch (e:NumberFormatException){
                Log.e("numberformat: ", e.toString() )
            }

        }
        return null
    }
}