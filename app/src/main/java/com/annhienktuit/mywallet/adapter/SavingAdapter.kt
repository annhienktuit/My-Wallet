package com.annhienktuit.mywallet.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.annhienktuit.mywallet.R
import com.annhienktuit.mywallet.activity.SavingActivity
import com.annhienktuit.mywallet.`object`.Saving
import com.annhienktuit.mywallet.activity.MainActivity
import kotlinx.android.synthetic.main.layout_saving_1.view.*
import kotlinx.android.synthetic.main.layout_saving_2.view.*
import java.text.DecimalFormat
import java.text.NumberFormat

class SavingAdapter(private val savingList: List<Saving>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class SavingViewHolder1(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name1: TextView = itemView.nameProduct1
        val money1: TextView = itemView.priceProduct1
        val progress1: ProgressBar = itemView.progressSaving1
    }
    class SavingViewHolder2(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name2: TextView = itemView.nameProduct2
        val money2: TextView = itemView.priceProduct2
        val progress2: ProgressBar = itemView.progressSaving2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView1 = LayoutInflater.from(parent.context).inflate(R.layout.layout_saving_1, parent, false)
        val itemView2 = LayoutInflater.from(parent.context).inflate(R.layout.layout_saving_2, parent, false)
        if (viewType == 0) {
            return SavingViewHolder1(itemView1)
        }
        return SavingViewHolder2(itemView2)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = savingList[position]
        if (position % 2 == 0) {
            var holder1 = holder as SavingViewHolder1
            holder1.name1.text = currentItem.nameOfProduct
            if (currentItem.moneyOfProduct != null)
                holder1.money1.text = changeToMoney(currentItem.moneyOfProduct)
            else
                holder1.money1.text = currentItem.moneyOfProduct
            var tmp1 = currentItem.currentSaving?.toLong()
            var tmp2 = currentItem.moneyOfProduct?.toLong()
            var result = (tmp1!! * 100) / tmp2!!
            holder1.progress1.progress = result.toInt()
            holder1.itemView.setOnClickListener {
                val intent = Intent(holder1.itemView.context, SavingActivity::class.java)
                intent.putExtra("position", position)
                holder1.itemView.context.startActivity(intent)
            }
        } else {
            var holder2 = holder as SavingViewHolder2
            holder2.name2.text = currentItem.nameOfProduct
            if (currentItem.moneyOfProduct != null)
                holder2.money2.text = changeToMoney(currentItem.moneyOfProduct)
            else
                holder2.money2.text = currentItem.moneyOfProduct
            var tmp1 = currentItem.currentSaving?.toLong()
            var tmp2 = currentItem.moneyOfProduct?.toLong()
            var result = (tmp1!! * 100) / tmp2!!
            holder2.progress2.progress = result.toInt()
            holder2.itemView.setOnClickListener {
                val intent = Intent(holder2.itemView.context, SavingActivity::class.java)
                intent.putExtra("position", position)
                holder2.itemView.context.startActivity(intent)
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return position % 2
    }

    override fun getItemCount() = savingList.size
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
