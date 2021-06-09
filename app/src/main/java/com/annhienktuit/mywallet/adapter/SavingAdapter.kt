package com.annhienktuit.mywallet.adapter

import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.annhienktuit.mywallet.R
import com.annhienktuit.mywallet.`object`.Saving
import com.annhienktuit.mywallet.activity.SavingActivity
import com.annhienktuit.mywallet.utils.FirebaseUtils
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.layout_saving_1.view.*
import kotlinx.android.synthetic.main.layout_saving_2.view.*
import java.text.DecimalFormat
import java.text.NumberFormat

class SavingAdapter(private val savingList: List<Saving>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val user: FirebaseUser? = FirebaseUtils.firebaseAuth.currentUser
    var ref = FirebaseDatabase
        .getInstance("https://my-wallet-80ed7-default-rtdb.asia-southeast1.firebasedatabase.app/")
        .getReference("datas").child(user?.uid.toString())
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
            val holder1 = holder as SavingViewHolder1
            holder1.name1.text = currentItem.nameOfProduct
            if (currentItem.moneyOfProduct != null)
                holder1.money1.text = changeToMoney(currentItem.moneyOfProduct)
            else
                holder1.money1.text = currentItem.moneyOfProduct

            val tmp1 = currentItem.currentSaving?.toLongOrNull()
            val tmp2 = currentItem.moneyOfProduct?.toLongOrNull()
            var result = 0
            if (tmp1 != null && tmp2 != null) {
                result = ((tmp1 * 100) / tmp2).toInt()
            }
            holder1.progress1.progress = result
        } else {
            val holder2 = holder as SavingViewHolder2
            holder2.name2.text = currentItem.nameOfProduct
            if (currentItem.moneyOfProduct != null)
                holder2.money2.text = changeToMoney(currentItem.moneyOfProduct)
            else
                holder2.money2.text = currentItem.moneyOfProduct
            val tmp1 = currentItem.currentSaving?.toLongOrNull()
            val tmp2 = currentItem.moneyOfProduct?.toLongOrNull()
            var result = 0
            if (tmp1 != null && tmp2 != null) {
                result = ((tmp1 * 100) / tmp2).toInt()
            }
            holder2.progress2.progress = result
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, SavingActivity::class.java)
            intent.putExtra("position", currentItem.index)
            intent.putExtra("current", currentItem.currentSaving)
            intent.putExtra("total", currentItem.moneyOfProduct)
            intent.putExtra("name", currentItem.nameOfProduct)
            holder.itemView.context.startActivity(intent)
        }
        holder.itemView.setOnLongClickListener {
            val builder = AlertDialog.Builder(holder.itemView.context)
            builder.setMessage("Do you want to delete this item?")
            builder.setPositiveButton("Yes")
            { dialog, which ->
                ref.child("savings").child("saving" + currentItem.index).removeValue()
            }
            builder.setNegativeButton("No")
            { dialog, which ->
                dialog.dismiss()
            }
            builder.create()
            builder.show()
            false
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

