package com.annhienktuit.mywallet.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.annhienktuit.mywallet.MainActivity
import com.annhienktuit.mywallet.R
import com.annhienktuit.mywallet.`object`.Wallet
import com.annhienktuit.mywallet.adapter.RecentTransactionAdapter
import com.annhienktuit.mywallet.adapter.WalletAdapter


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class HomeFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null


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
        var walletList = (activity as MainActivity?)!!.getWalletList()
        var recyclerWallet = view.findViewById(R.id.recyclerWallet) as RecyclerView
        recyclerWallet.adapter = WalletAdapter(walletList)
        recyclerWallet.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recyclerWallet.setHasFixedSize(true)
        var transactionList = (activity as MainActivity?)!!.getTransactionList()
        var recyclerTransaction = view.findViewById(R.id.recyclerTransaction) as RecyclerView
        recyclerTransaction.adapter = RecentTransactionAdapter(transactionList)
        recyclerTransaction.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerTransaction.setHasFixedSize(true)
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
}