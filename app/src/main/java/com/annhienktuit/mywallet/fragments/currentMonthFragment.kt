package com.annhienktuit.mywallet.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.annhienktuit.mywallet.R
import com.annhienktuit.mywallet.`object`.Income
import com.annhienktuit.mywallet.activity.MainActivity
import com.annhienktuit.mywallet.utils.FirebaseUtils
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Pie
import com.anychart.enums.Align
import com.anychart.enums.LegendLayout
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_current_month.*
import java.util.*


class currentMonthFragment : Fragment() {

    val user: FirebaseUser? = FirebaseUtils.firebaseAuth.currentUser
    var ref = FirebaseDatabase
        .getInstance("https://my-wallet-80ed7-default-rtdb.asia-southeast1.firebasedatabase.app/")
        .getReference("datas")
    lateinit var pieChart: AnyChartView
    lateinit var testText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootview: View = inflater.inflate(R.layout.fragment_current_month, container, false)

        setIncomePieChartData(rootview)

        return rootview
    }

    private fun setIncomePieChartData(rootview: View) {
        testText = rootview.findViewById(R.id.testText)

        //reference of Income
        val refIncome = ref.child(user?.uid.toString()).child("transactions").orderByChild("inorout").equalTo("true")

        //list of current month income
        var listCurrentIncome = mutableListOf<Income>()

        refIncome.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //set pie chart
                pieChart = rootview.findViewById(R.id.pieChart)
                var pie: Pie = AnyChart.pie()

                var listPieChartData = mutableListOf<DataEntry>()

                //listCurrentIncome.removeAll(listCurrentIncome)
                for(childBranch in snapshot.children){
                    listCurrentIncome.add(Income(
                        childBranch.child("category").value.toString(),
                        childBranch.child("money").value.toString(),
                        childBranch.child("currentMonth").value.toString()
                    ))
                }

                //listCurrentIncome = handleIncomeList(listCurrentIncome)

                for(item in listCurrentIncome){
                    testText.append(" cat:" + item.category + " money:" + item.moneyAmount + " month:" + item.currentMonth)
                    listPieChartData.add(ValueDataEntry(item.category, item.moneyAmount.toLong()))
                }

                pie.data(listPieChartData)

                pie.title("Current Month Income")

                pie.labels().position("outside")

                pie.legend().title().enabled(true)
                pie.legend().title()
                    .text("Retail channels")
                    .padding(0.0, 0.0, 10.0, 0.0)

                pie.legend()
                    .position("center-bottom")
                    .itemsLayout(LegendLayout.HORIZONTAL)
                    .align(Align.CENTER)

                pieChart.setChart(pie)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    private fun handleIncomeList(list: MutableList<Income>) : MutableList<Income>{
        var now: Calendar = Calendar.getInstance()
        var currentMonth = now.get(Calendar.MONTH + 1)

        for(item in list){
            if(item.currentMonth == "null")
                item.currentMonth = "-1"

            if(item.currentMonth != currentMonth.toString()){
                list.remove(item)
            }
        }

        for(i in list.indices){
            var j = i + 1
            for(j in list.indices){
                if(list[j].category == list[i].category){
                    var moneyTemp: Long = list[i].moneyAmount.toLong()
                    moneyTemp += list[j].moneyAmount.toLong()

                    list[i].moneyAmount = moneyTemp.toString()
                    list.remove(list[j])
                }
            }
        }

        return list
    }
}