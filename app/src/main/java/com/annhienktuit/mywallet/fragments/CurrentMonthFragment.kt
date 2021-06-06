package com.annhienktuit.mywallet.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.annhienktuit.mywallet.R
import com.annhienktuit.mywallet.`object`.Transaction
import com.annhienktuit.mywallet.utils.FirebaseUtils
import com.anychart.APIlib
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


class CurrentMonthFragment : Fragment() {

    private val user: FirebaseUser? = FirebaseUtils.firebaseAuth.currentUser
    var ref = FirebaseDatabase
        .getInstance("https://my-wallet-80ed7-default-rtdb.asia-southeast1.firebasedatabase.app/")
        .getReference("datas")
    lateinit var pieIncomeChart: AnyChartView
    lateinit var pieExpenseChart: AnyChartView
    lateinit var currentBalance: TextView
    lateinit var currentIncome: TextView
    lateinit var currentExpense: TextView

    var amountBalance = 0L
    var amountIncome = 0L
    var amountExpense = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView: View = inflater.inflate(R.layout.fragment_current_month, container, false)

        setData(rootView)
        setIncomePieChartData(rootView)
        setExpensePieChartData(rootView)

        return rootView
    }

    private fun setData(rootView: View){
        currentBalance = rootView.findViewById(R.id.balance)
        currentExpense = rootView.findViewById(R.id.expense)
        currentIncome = rootView.findViewById(R.id.income)
    }

    private fun setIncomePieChartData(rootview: View) {

        //reference of Income
        val refIncome = ref.child(user?.uid.toString()).child("transactions").orderByChild("inorout").equalTo("true")

        //list of current month income
        var listCurrentIncome = mutableListOf<Transaction>()

        refIncome.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //set pie chart
                pieIncomeChart = rootview.findViewById(R.id.pieChartIncome)
                APIlib.getInstance().setActiveAnyChartView(pieIncomeChart)
                var pie: Pie = AnyChart.pie()

                var listPieChartData = mutableListOf<DataEntry>()

                //listCurrentIncome.removeAll(listCurrentIncome)
                for(childBranch in snapshot.children){
                    listCurrentIncome.add(Transaction(
                        childBranch.child("category").value.toString(),
                        childBranch.child("money").value.toString(),
                        childBranch.child("currentMonth").value.toString()
                    ))
                }

                listCurrentIncome = handleListForChart(listCurrentIncome)

                for(item in listCurrentIncome){
                    amountIncome += item.moneyAmount.toLong()
                    listPieChartData.add(ValueDataEntry(item.category, item.moneyAmount.toLong()))
                }

                amountBalance += amountIncome
                currentIncome.text = amountIncome.toString()
                currentBalance.text = amountBalance.toString()

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

                pieIncomeChart.setChart(pie)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    private fun setExpensePieChartData(rootview: View) {
        //reference of Income
        val refExs = ref.child(user?.uid.toString()).child("transactions").orderByChild("inorout").equalTo("false")

        //list of current month income
        var listCurrentExpense = mutableListOf<Transaction>()

        refExs.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //set pie chart
                pieExpenseChart = rootview.findViewById(R.id.pieChartExpense)
                APIlib.getInstance().setActiveAnyChartView(pieExpenseChart)
                var pie: Pie = AnyChart.pie()

                var listPieChartData = mutableListOf<DataEntry>()

                //listCurrentIncome.removeAll(listCurrentIncome)
                for(childBranch in snapshot.children){
                    listCurrentExpense.add(Transaction(
                        childBranch.child("category").value.toString(),
                        childBranch.child("money").value.toString(),
                        childBranch.child("currentMonth").value.toString()
                    ))
                }

                listCurrentExpense = handleListForChart(listCurrentExpense)

                for(item in listCurrentExpense){
                    amountExpense += item.moneyAmount.toLong()
                    listPieChartData.add(ValueDataEntry(item.category, item.moneyAmount.toLong()))
                }

                amountBalance -= amountExpense
                currentBalance.text = amountBalance.toString()
                currentExpense.text = amountExpense.toString()

                pie.data(listPieChartData)

                pie.title("Current Month Expense")

                pie.labels().position("outside")

                pie.legend().title().enabled(true)
                pie.legend().title()
                    .text("Retail channels")
                    .padding(0.0, 0.0, 10.0, 0.0)

                pie.legend()
                    .position("center-bottom")
                    .itemsLayout(LegendLayout.HORIZONTAL)
                    .align(Align.CENTER)

                pieExpenseChart.setChart(pie)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


    private fun handleListForChart(list: MutableList<Transaction>) : MutableList<Transaction>{
        var now: Calendar = Calendar.getInstance()
        var currentMonth = now.get(Calendar.MONTH) + 1

        var iForCurrentMonth = 0
        while(iForCurrentMonth < list.size){
            if(list[iForCurrentMonth].currentMonth != currentMonth.toString()){
                list.remove(list[iForCurrentMonth])
            }
            iForCurrentMonth++
        }

        var i = 0;
        while(i < list.size){
            var j = i + 1
            while(j < list.size){
               if(list[j].category == list[i].category){
                   var moneyTemp: Long = list[i].moneyAmount.toLong()
                   moneyTemp += list[j].moneyAmount.toLong()
                   list[i].moneyAmount = moneyTemp.toString()
                   list.remove(list[j])
               }
                j++
            }
            i++
        }

        return list
    }
}