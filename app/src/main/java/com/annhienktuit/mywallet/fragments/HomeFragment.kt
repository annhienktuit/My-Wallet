package com.annhienktuit.mywallet.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.annhienktuit.mywallet.R
import com.annhienktuit.mywallet.activity.MainActivity
import com.annhienktuit.mywallet.activity.TransactionActivity
import com.annhienktuit.mywallet.adapter.RecentTransactionAdapter
import com.annhienktuit.mywallet.utils.Extensions.changeToMoney
import kotlinx.android.synthetic.main.fragment_home.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class HomeFragment : Fragment() {
    lateinit var data: MainActivity

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
        var name = data.getName()
        var balance = data.getBalance()
        var income = data.getIncome()
        var expense = data.getExpense()
        var recyclerTransaction = view.findViewById(R.id.recyclerTransaction) as RecyclerView
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
}

