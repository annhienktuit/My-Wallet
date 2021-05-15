package com.annhienktuit.mywallet.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.annhienktuit.mywallet.MainActivity
import com.annhienktuit.mywallet.R
import com.annhienktuit.mywallet.`object`.Saving
import com.annhienktuit.mywallet.`object`.Wallet
import com.annhienktuit.mywallet.adapter.SavingAdapter
import com.annhienktuit.mywallet.adapter.WalletAdapter

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class PlanningFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    var walletList = ArrayList<Wallet>()
    var savingList = ArrayList<Saving>()
    lateinit var recyclerWallet: RecyclerView
    lateinit var recyclerSaving: RecyclerView

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
        var view = inflater.inflate(R.layout.fragment_planning, container, false)
        recyclerWallet = view.findViewById(R.id.recyclerWalletDetail)
        recyclerSaving = view.findViewById(R.id.recyclerSavings)
        createWallets()
        createSavings()
        return view
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PlanningFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    fun createWallets() {
        walletList = (activity as MainActivity?)!!.getWalletList() as ArrayList<Wallet>
        recyclerWallet.adapter = WalletAdapter(walletList)
        recyclerWallet.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recyclerWallet.setHasFixedSize(true)
    }
    fun createSavings() {
        savingList.add(Saving("Buy iPhone 13 ", "33,500,000", "11,000,000"))
        savingList.add(Saving("Buy Crypto", "13,745,000,000", "0"))
        recyclerSaving.adapter = SavingAdapter(savingList)
        recyclerSaving.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerSaving.setHasFixedSize(true)
    }
}