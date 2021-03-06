package com.annhienktuit.mywallet.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.compose.ui.window.Dialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.annhienktuit.mywallet.R
import com.annhienktuit.mywallet.activity.MainActivity
import com.annhienktuit.mywallet.activity.TransactionActivity
import com.annhienktuit.mywallet.adapter.RecentTransactionAdapter
import com.annhienktuit.mywallet.utils.Extensions.changeToMoney
import kotlinx.android.synthetic.main.activity_saving.view.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class HomeFragment : Fragment() {
    lateinit var data: MainActivity
    lateinit var name: String
    lateinit var balance: String
    lateinit var income: String
    lateinit var expense: String


    lateinit var transactionAdapter: RecentTransactionAdapter

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val pref = this.requireActivity()
            .getSharedPreferences("pref", Context.MODE_PRIVATE)
        Log.i("NotiFrag: ", pref.getString("status","").toString())

        val btnSeeAllTransaction = view.findViewById<Button>(R.id.btnSeeAll)
        btnSeeAllTransaction.setOnClickListener {
            val intent = Intent(activity, TransactionActivity::class.java)
            startActivity(intent)
        }
        setData(view)
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    fun setData(view: View) {
        data = (activity as MainActivity)
        transactionAdapter = data.getTransactionAdapter()
        name = data.getName().toString()
        balance = data.getBalance().toString()
        income = data.getIncome().toString()
        expense = data.getExpense().toString()
        var recyclerTransaction = view.findViewById(R.id.recyclerTransaction) as RecyclerView
        setSwipeToDelete(recyclerTransaction)
        recyclerTransaction.adapter = transactionAdapter
        recyclerTransaction.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerTransaction.setHasFixedSize(true)
        var txtName = view.findViewById<TextView>(R.id.txtName)
        var txtBalance = view.findViewById<TextView>(R.id.textBalance)
        var txtIncome = view.findViewById<TextView>(R.id.textIncome)
        var txtExpense = view.findViewById<TextView>(R.id.textExpense)
        txtName.text = name
        txtBalance.text = changeToMoney(balance) + " VND"
        txtIncome.text = changeToMoney(income)
        txtExpense.text = changeToMoney(expense)
    }
    private fun setSwipeToDelete(rv: RecyclerView) {
        val simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object :
            ItemTouchHelper.SimpleCallback(30, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                //Remove swiped item from list and notify the RecyclerView
                val position = viewHolder.adapterPosition
                if (!transactionAdapter.isSavingTran(position)) {
                    val dialog = AlertDialog.Builder(context)
                    dialog.setTitle("Confirm")
                    dialog.setIcon(R.drawable.ic_baseline_warning_24)
                    dialog.setMessage("Do you want to delete this transaction?")
                    dialog.setPositiveButton("OK") { dialog, which ->
                        transactionAdapter.deleteItem(position, balance, income, expense)
                    }
                    dialog.setNegativeButton("Cancel") { dialog, which ->
                        dialog.dismiss()
                        rv.adapter!!.notifyDataSetChanged()
                    }
                    dialog.show()
                } else {
                    val dialog = AlertDialog.Builder(context)
                    dialog.setMessage("Please go to saving to delete this transaction!")
                    dialog.setPositiveButton("OK") { dialog, which ->
                        dialog.dismiss()
                        rv.adapter!!.notifyDataSetChanged()
                    }
                    dialog.show()
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(rv)
    }
}

