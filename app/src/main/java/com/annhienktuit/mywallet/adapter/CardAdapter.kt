package com.annhienktuit.mywallet.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.annhienktuit.mywallet.activity.CardActivity
import com.annhienktuit.mywallet.R
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
        var holder1 = holder as CardViewHolder
        holder1.card.text = currentItem.nameCard
        holder1.person.text = currentItem.namePerson
        holder1.bank.text = currentItem.bankName
        holder1.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, CardActivity::class.java)
            intent.putExtra("position", position)
            holder1.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount() = cardList.size
}