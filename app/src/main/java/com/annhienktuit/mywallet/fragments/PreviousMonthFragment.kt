package com.annhienktuit.mywallet.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.annhienktuit.mywallet.R
import com.annhienktuit.mywallet.`object`.DetailTransaction
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
import java.util.*

class PreviousMonthFragment : Fragment() {

    private val user: FirebaseUser? = FirebaseUtils.firebaseAuth.currentUser
    var ref = FirebaseDatabase
        .getInstance("https://my-wallet-80ed7-default-rtdb.asia-southeast1.firebasedatabase.app/")
        .getReference("datas")
    lateinit var pieIncomeChart: AnyChartView
    lateinit var pieExpenseChart: AnyChartView
    lateinit var previousBalance: TextView
    lateinit var previousIncome: TextView
    lateinit var previousExpense: TextView
    lateinit var previousDebt: TextView
    lateinit var previousLoan: TextView


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val rootView: View = inflater.inflate(R.layout.fragment_previous_month, container, false)

        setData(rootView)
        setIncomePieChartData(rootView)
        setExpensePieChartData(rootView)

        return rootView
    }

    private fun setData(rootView: View){
        previousBalance = rootView.findViewById(R.id.balance)
        previousExpense = rootView.findViewById(R.id.expense)
        previousIncome = rootView.findViewById(R.id.income)
        previousDebt = rootView.findViewById(R.id.debt)
        previousLoan = rootView.findViewById(R.id.loan)
    }

    private fun setIncomePieChartData(rootView: View) {
        //reference of Income
        val refIncome = ref.child(user?.uid.toString()).child("transactions").orderByChild("inorout").equalTo("true")

        //list of previous month income
        var listPreviousIncome = mutableListOf<DetailTransaction>()

        refIncome.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var amountIncome = 0L
                var amountDebt = 0L
                //set pie chart
                pieIncomeChart = rootView.findViewById(R.id.pieChartIncome)
                APIlib.getInstance().setActiveAnyChartView(pieIncomeChart)
                var pie: Pie = AnyChart.pie()

                var listPieChartData = mutableListOf<DataEntry>()

                listPreviousIncome.removeAll(listPreviousIncome)
                for(childBranch in snapshot.children){
                    listPreviousIncome.add(
                        DetailTransaction(
                        childBranch.child("category").value.toString(),
                        childBranch.child("money").value.toString(),
                        childBranch.child("currentMonth").value.toString()
                    )
                    )
                }


                listPreviousIncome = handleListForChart(listPreviousIncome)


                for(item in listPreviousIncome){
                    if(item.category == "Debt"){
                        amountDebt += item.moneyAmount.toLong()
                    }

                    amountIncome += item.moneyAmount.toLong()
                    listPieChartData.add(ValueDataEntry(item.category, item.moneyAmount.toLong()))
                }

                previousIncome.text = amountIncome.toString()
                previousDebt.text = amountDebt.toString()

                previousBalance.text = (previousIncome.text.toString().toLong() - previousExpense.text.toString().toLong()).toString()
                previousBalance.append(" VND")

                pie.data(listPieChartData)

                pie.title("Previous Month Income")

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

    private fun setExpensePieChartData(rootView: View) {
        //reference of Income
        val refExs = ref.child(user?.uid.toString()).child("transactions").orderByChild("inorout").equalTo("false")

        //list of previous month income
        var listPreviousExpense = mutableListOf<DetailTransaction>()

        refExs.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var amountExpense = 0L
                var amountLoan = 0L
                //set pie chart
                pieExpenseChart = rootView.findViewById(R.id.pieChartExpense)
                APIlib.getInstance().setActiveAnyChartView(pieExpenseChart)
                var pie: Pie = AnyChart.pie()

                var listPieChartData = mutableListOf<DataEntry>()

                listPreviousExpense.removeAll(listPreviousExpense)
                for(childBranch in snapshot.children){
                    listPreviousExpense.add(DetailTransaction(
                        childBranch.child("category").value.toString(),
                        childBranch.child("money").value.toString(),
                        childBranch.child("currentMonth").value.toString()
                    ))
                }

                listPreviousExpense = handleListForChart(listPreviousExpense)

                for(item in listPreviousExpense){
                    if(item.category == "Loan"){
                        amountLoan += item.moneyAmount.toLong()
                    }

                    amountExpense += item.moneyAmount.toLong()
                    listPieChartData.add(ValueDataEntry(item.category, item.moneyAmount.toLong()))
                }

                previousExpense.text = amountExpense.toString()
                previousLoan.text = amountLoan.toString()

                previousBalance.text = (previousIncome.text.toString().toLong() - previousExpense.text.toString().toLong()).toString()
                previousBalance.append(" VND")

                pie.data(listPieChartData)

                pie.title("Previous Month Expense")

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


    private fun handleListForChart(list: MutableList<DetailTransaction>) : MutableList<DetailTransaction>{
        var now: Calendar = Calendar.getInstance()
        var previousMonth = now.get(Calendar.MONTH)

        if(previousMonth == 0)
            previousMonth = 12


        var iForPreviousMonth = 0

        while(iForPreviousMonth < list.size){
            if(list[iForPreviousMonth].currentMonth != previousMonth.toString()){
                list.remove(list[iForPreviousMonth])
                iForPreviousMonth--
            }
            iForPreviousMonth++
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
                    j--
                }
                j++
            }
            i++
        }

        return list
    }

}