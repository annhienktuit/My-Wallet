package com.annhienktuit.mywallet.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.annhienktuit.mywallet.R
import com.annhienktuit.mywallet.`object`.DetailTransaction
import com.annhienktuit.mywallet.activity.MainActivity
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
    lateinit var progressIncomeBar: ProgressBar
    lateinit var progressExpenseBar: ProgressBar


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val rootView: View = inflater.inflate(R.layout.fragment_previous_month, container, false)

        setData(rootView)
        setIncomePieChartData()
        setExpensePieChartData()

        return rootView
    }

    private fun setData(rootView: View){
        progressIncomeBar = rootView.findViewById(R.id.progressIncomeBar)
        progressExpenseBar = rootView.findViewById(R.id.progressExpenseBar)
        previousBalance = rootView.findViewById(R.id.balance)
        previousExpense = rootView.findViewById(R.id.expense)
        previousIncome = rootView.findViewById(R.id.income)
        previousDebt = rootView.findViewById(R.id.debt)
        previousLoan = rootView.findViewById(R.id.loan)
        pieIncomeChart = rootView.findViewById(R.id.pieChartIncome)
        pieExpenseChart = rootView.findViewById(R.id.pieChartExpense)
    }

    private fun setIncomePieChartData() {
        val data = (activity as MainActivity)

        var listPieChartData = data.getPreviousIncomeData()
        var amountPreviousDebt = data.getPreviousDebt()
        var amountPreviousIncome = data.getPreviousIncome()

        previousIncome.text = amountPreviousIncome.toString()
        previousDebt.text = amountPreviousDebt.toString()
        previousBalance.text = (previousIncome.text.toString().toLong() - previousExpense.text.toString().toLong()).toString()
        previousBalance.append(" VND")

        pieIncomeChart.setProgressBar(progressIncomeBar)
        APIlib.getInstance().setActiveAnyChartView(pieIncomeChart)
        var pie: Pie = AnyChart.pie()

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

    private fun setExpensePieChartData() {
        val data = (activity as MainActivity)

        var listPieChartData = data.getPreviousExpenseData()
        var amountPreviousLoan = data.getPreviousLoan()
        var amountPreviousExpense = data.getPreviousExpense()

        previousExpense.text = amountPreviousExpense.toString()
        previousLoan.text = amountPreviousLoan.toString()
        previousBalance.text = (previousIncome.text.toString().toLong() - previousExpense.text.toString().toLong()).toString()
        previousBalance.append(" VND")

        pieExpenseChart.setProgressBar(progressExpenseBar)
        APIlib.getInstance().setActiveAnyChartView(pieExpenseChart)
        var pie: Pie = AnyChart.pie()

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

}