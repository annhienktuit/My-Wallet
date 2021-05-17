package com.annhienktuit.mywallet

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.activity_interest_rate.*
import kotlinx.android.synthetic.main.dialog_type_of_interest_rate.*
import java.lang.NullPointerException

class TypeOfInterestDialog: DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var rootView: View = inflater.inflate(R.layout.dialog_type_of_interest_rate, container, false)

        val submitBtn: Button = rootView.findViewById(R.id.submitBtn) as Button
        val radioGroup: RadioGroup = rootView.findViewById(R.id.TypeOfInterestRadioGroup) as RadioGroup

        submitBtn.setOnClickListener{
            try{
                var id: Int = radioGroup.checkedRadioButtonId
                val selectedRadioBtn: RadioButton = rootView.findViewById(id) as RadioButton

                var selectedType: String = selectedRadioBtn.text.toString()
                //Pass data to Interest Rate Activity
                (activity as InterestRateActivity).tvTypeOfInterest.text = selectedType
                dismiss()
            }
            catch(e: NullPointerException){
                Toast.makeText(activity, "Please select at least one type of interest", android.widget.Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }

        return rootView
    }


}