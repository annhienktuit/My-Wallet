package com.annhienktuit.mywallet.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.annhienktuit.mywallet.R
import com.annhienktuit.mywallet.`object`.Card
import com.annhienktuit.mywallet.`object`.Limitation
import com.annhienktuit.mywallet.`object`.Saving
import com.annhienktuit.mywallet.`object`.Wallet
import com.annhienktuit.mywallet.activity.MainActivity
import com.annhienktuit.mywallet.adapter.CardAdapter
import com.annhienktuit.mywallet.adapter.SavingAdapter
import com.annhienktuit.mywallet.adapter.WalletAdapter
import com.annhienktuit.mywallet.dialog.AddLimitationDialog
import com.google.android.gms.dynamic.SupportFragmentWrapper
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.activity_saving.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_planning.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class PlanningFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    var walletList = ArrayList<Wallet>()
    var savingList = ArrayList<Saving>()
    var cardList = ArrayList<Card>()
    var limitationList = ArrayList<Limitation>()

    lateinit var recyclerWallet: RecyclerView
    lateinit var recyclerSaving: RecyclerView
    lateinit var recyclerCard: RecyclerView
    lateinit var recyclerLimitation: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        //open add limitation dialog

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_planning, container, false)

        val btnAddLimitation: MaterialButton = view.findViewById(R.id.btnAddLimitation) as MaterialButton

        //open add limitation dialog
        btnAddLimitation.setOnClickListener {
            var dialog = AddLimitationDialog()
            dialog.show(childFragmentManager, "AddLimitationDialog")
        }

        setData(view)
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
    fun setData(view: View) {
        var data = (activity as MainActivity)
        walletList = data.getWalletList()!!
        savingList = data.getSavingList()!!
        cardList = data.getCardList()!!
        //limitationList = data.getLimitationList()!!

        recyclerWallet = view.findViewById(R.id.recyclerWalletDetail)
        recyclerSaving = view.findViewById(R.id.recyclerSavings)
        recyclerCard = view.findViewById(R.id.recyclerCards)
        recyclerLimitation = view.findViewById(R.id.recyclerLimitation)

        recyclerWallet.adapter = WalletAdapter(walletList)
        recyclerWallet.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recyclerWallet.setHasFixedSize(true)

        recyclerSaving.adapter = SavingAdapter(savingList)
        recyclerSaving.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerSaving.setHasFixedSize(true)

        recyclerCard.adapter = CardAdapter(cardList)
        recyclerCard.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerCard.setHasFixedSize(true)

        //recyclerLimitation.adapter = LimitationAdapter(limitationList)
        recyclerLimitation.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerLimitation.setHasFixedSize(true)
    }
}