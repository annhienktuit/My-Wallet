package com.annhienktuit.mywallet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.annhienktuit.mywallet.R
import com.annhienktuit.mywallet.`object`.RecentTransaction
import com.annhienktuit.mywallet.`object`.Saving
import kotlinx.android.synthetic.main.layout_recent_transaction_1.view.*
import kotlinx.android.synthetic.main.layout_recent_transaction_2.view.*
import kotlinx.android.synthetic.main.layout_saving_1.view.*
import kotlinx.android.synthetic.main.layout_saving_2.view.*
import kotlinx.android.synthetic.main.layout_wallet_item_2.view.*

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
        if (viewType == 0) {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_saving_1, parent, false)
            return SavingViewHolder1(itemView)
        }
        else{
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_saving_2, parent, false)
            return SavingViewHolder2(itemView)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = savingList[position]
        if (position % 2 == 0) {
            var holder = holder as SavingViewHolder1
            holder.name1.text = currentItem.nameOfProduct
            holder.money1.text = currentItem.moneyOfProduct
            holder.progress1.progress = 50
        } else {
            var holder = holder as SavingViewHolder2
            holder.name2.text = currentItem.nameOfProduct
            holder.money2.text = currentItem.moneyOfProduct
            holder.progress2.progress = 90
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position % 2
    }

    override fun getItemCount() = savingList.size
}
