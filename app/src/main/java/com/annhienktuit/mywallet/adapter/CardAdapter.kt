package com.annhienktuit.mywallet.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.annhienktuit.mywallet.CardActivity
import com.annhienktuit.mywallet.R
import com.annhienktuit.mywallet.SavingActivity
import com.annhienktuit.mywallet.`object`.Card
import kotlinx.android.synthetic.main.layout_card_planning.view.*

class CardAdapter(private val cardList: List<Card>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card: TextView = itemView.cardName
        val person: TextView = itemView.personName
        val bank: TextView = itemView.bankName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_card_planning, parent, false)
        return CardViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = cardList[position]
        var holder = holder as CardViewHolder
        holder.card.text = currentItem.nameCard
        holder.person.text = currentItem.namePerson
        holder.bank.text = currentItem.nameBanking
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, CardActivity::class.java)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount() = cardList.size
}