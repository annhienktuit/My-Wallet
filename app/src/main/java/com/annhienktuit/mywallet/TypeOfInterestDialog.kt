package com.annhienktuit.mywallet

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_type_of_interest_rate.*

class TypeOfInterestDialog: DialogFragment() {
    lateinit var type: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var rootView: View = inflater.inflate(R.layout.dialog_type_of_interest_rate, container, false)

//        TypeOfInterestRadioGroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
//            if(SimpleInterestRadioBtn.isChecked)
//                type = SimpleInterestRadioBtn.text.toString()
//            else if(CompoundInterestRadioBtn.isChecked)
//                type = CompoundInterestRadioBtn.text.toString()
//        })

        return rootView
    }

    fun getInterestType() : String{
        return type
    }
}