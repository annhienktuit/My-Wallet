package com.annhienktuit.mywallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment

class DoneInterestDialog: DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.dialog_done_interest_rate, container, false)

        val closeBtn: Button = rootView.findViewById(R.id.closeBtn) as Button

        closeBtn.setOnClickListener{
            dismiss()
        }

        return rootView
    }
}