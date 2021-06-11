package com.annhienktuit.mywallet.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
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
    lateinit var currentDebt: TextView
    lateinit var currentLoan: TextView
    lateinit var progressIncomeBar: ProgressBar
    lateinit var progressExpenseBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView: View = inflater.inflate(R.layout.fragment_current_month, container, false)

        setData(rootView)
        setIncomePieChartData()
        setExpensePieChartData()

        return rootView
    }

    private fun setData(rootView: View){
        progressIncomeBar = rootView.findViewById(R.id.progressIncomeBar)
        progressExpenseBar = rootView.findViewById(R.id.progressExpenseBar)
        currentBalance = rootView.findViewById(R.id.balance)
        currentExpense = rootView.findViewById(R.id.expense)
        currentIncome = rootView.findViewById(R.id.income)
        currentDebt = rootView.findViewById(R.id.debt)
        currentLoan = rootView.findViewById(R.id.loan)
        pieIncomeChart = rootView.findViewById(R.id.pieChartIncome)
        pieExpenseChart = rootView.findViewById(R.id.pieChartExpense)
    }

    private fun setIncomePieChartData() {
        val data = (activity as MainActivity)

        var listPieChartData = data.getCurrentIncomeData()
        var amountCurrentDebt = data.getCurrentDebt()
        var amountCurrentIncome = data.getCurrentIncome()

        currentIncome.text = amountCurrentIncome.toString()
        currentDebt.text = amountCurrentDebt.toString()
        currentBalance.text = (currentIncome.text.toString().toLong() - currentExpense.text.toString().toLong()).toString()
        currentBalance.append(" VND")

        pieIncomeChart.setProgressBar(progressIncomeBar)
        APIlib.getInstance().setActiveAnyChartView(pieIncomeChart)
        var pie: Pie = AnyChart.pie()


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

    private fun setExpensePieChartData() {
        val data = (activity as MainActivity)

        var listPieChartData = data.getCurrentExpenseData()
        var amountCurrentLoan = data.getCurrentLoan()
        var amountCurrentExpense = data.getCurrentExpense()

        currentExpense.text = amountCurrentExpense.toString()
        currentLoan.text = amountCurrentLoan.toString()
        currentBalance.text = (currentIncome.text.toString().toLong() - currentExpense.text.toString().toLong()).toString()
        currentBalance.append(" VND")

        pieExpenseChart.setProgressBar(progressExpenseBar)
        APIlib.getInstance().setActiveAnyChartView(pieExpenseChart)
        var pie: Pie = AnyChart.pie()


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
}