package com.annhienktuit.mywallet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.annhienktuit.mywallet.R
import com.annhienktuit.mywallet.`object`.DetailTransaction

class AllMonthAdapter(private val context: Context, private val dataSource: MutableList<DetailTransaction>) : BaseAdapter() {
    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = inflater.inflate(R.layout.layout_all_month_list_item, parent, false)

        val monthYear = rowView.findViewById<TextView>(R.id.tvMonthYear)
        val balance = rowView.findViewById<TextView>(R.id.tvBalance)
        val mood = rowView.findViewById<ImageView>(R.id.ivMood)

        val trans = getItem(position) as DetailTransaction

        monthYear.append("${trans.currentMonth}/${trans.currentYear}")
        balance.text = trans.moneyAmount
        if(trans.moneyAmount.toLong() < 0){
            mood.setImageResource(R.drawable.ic_baseline_mood_bad_24)
        }
        else{
            mood.setImageResource(R.drawable.ic_baseline_mood_24)
        }

        return rowView
    }

}