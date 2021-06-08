package com.annhienktuit.mywallet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.annhienktuit.mywallet.R
import com.annhienktuit.mywallet.`object`.TransactionForReport

class AllMonthAdapter(private val context: Context, private val dataSource: MutableList<TransactionForReport>) : BaseAdapter() {
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

        return rowView
    }

}