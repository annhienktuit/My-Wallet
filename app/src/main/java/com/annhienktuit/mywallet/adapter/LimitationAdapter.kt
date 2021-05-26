package com.annhienktuit.mywallet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.annhienktuit.mywallet.R
import com.annhienktuit.mywallet.`object`.Limitation

class LimitationAdapter(val context: Context, val LimitationList: ArrayList<Limitation>) : RecyclerView.Adapter<LimitationAdapter.LimitationViewHolder>() {
    inner class LimitationViewHolder(val v: View): RecyclerView.ViewHolder(v){
        val title: TextView = v.findViewById(R.id.tvTitle)
        //val subtitle: TextView = v.findViewById(R.id.tvSubTitle)
        val target: TextView = v.findViewById(R.id.tvTarget)
        //val ivGroup: ImageView = v.findViewById(R.id.ivGroup)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LimitationViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.layout_limitation_item, parent, false)
        return LimitationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LimitationViewHolder, position: Int) {
        val newList = LimitationList[position]
        holder.title.text = newList.limitedGroup
        //holder.subtitle.text = newList.moneyLeft.toString()
        holder.target.text = newList.target.toString()
    }

    override fun getItemCount(): Int {
        return LimitationList.size
    }
}