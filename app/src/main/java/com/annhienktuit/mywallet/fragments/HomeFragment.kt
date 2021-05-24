package com.annhienktuit.mywallet.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.annhienktuit.mywallet.R
import com.annhienktuit.mywallet.`object`.RecentTransaction
import com.annhienktuit.mywallet.`object`.Saving
import com.annhienktuit.mywallet.`object`.Wallet
import com.annhienktuit.mywallet.activity.MainActivity
import com.annhienktuit.mywallet.activity.SplashScreenActivity
import com.annhienktuit.mywallet.adapter.RecentTransactionAdapter
import com.annhienktuit.mywallet.adapter.WalletAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_home.*
import org.w3c.dom.Text


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class HomeFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    var walletList = ArrayList<Wallet>()
    var transactionList = ArrayList<RecentTransaction>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_home, container, false)
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
        var data = (activity as MainActivity)
        var tmp = data.getData()
        walletList = data.getWalletList()!!
        transactionList = data.getTransactionList()!!
        var recyclerWallet = view.findViewById(R.id.recyclerWallet) as RecyclerView
        recyclerWallet.adapter = WalletAdapter(walletList)
        recyclerWallet.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recyclerWallet.setHasFixedSize(true)
        var recyclerTransaction = view.findViewById(R.id.recyclerTransaction) as RecyclerView
        recyclerTransaction.adapter = RecentTransactionAdapter(transactionList)
        recyclerTransaction.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerTransaction.setHasFixedSize(true)
        var txtName = view.findViewById<TextView>(R.id.txtName)
        var txtBalance = view.findViewById<TextView>(R.id.textBalance)
        var txtIncome = view.findViewById<TextView>(R.id.textIncome)
        var txtExpense = view.findViewById<TextView>(R.id.textExpense)
        if (tmp != null) {
            txtName.text = tmp.get("name").toString()
            txtBalance.text = tmp.get("balance").toString() + " VND"
            txtIncome.text = tmp.get("income").toString()
            txtExpense.text = tmp.get("expense").toString()
        }
    }
}

