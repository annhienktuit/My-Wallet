package com.annhienktuit.mywallet.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.annhienktuit.mywallet.R

class AddLimitationDialog: DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.dialog_add_limitation, container, false)

        val saveBtn: Button = rootView.findViewById(R.id.saveBtn) as Button

        saveBtn.setOnClickListener {
            dismiss()
        }

        return rootView
    }
}