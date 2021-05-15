package com.annhienktuit.mywallet.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.annhienktuit.mywallet.*
import com.annhienktuit.mywallet.utils.FirebaseUtils.firebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_user.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_user, container, false)
        val btnLogin = view?.findViewById<Button>(R.id.btnSignIn)
        btnLogin?.setOnClickListener {
            activity?.let {
                val intent = Intent(it, LoginActivity::class.java)
                it.startActivity(intent)
            }
        }
        val btnSignUp = view?.findViewById<Button>(R.id.btnSignUp)
        btnSignUp?.setOnClickListener {
            activity?.let {
                val intent = Intent(it, SignUpActivity::class.java)
                it.startActivity(intent)
            }
        }
        val btnSignOut = view?.findViewById<Button>(R.id.btnLogOut)
        btnSignOut?.setOnClickListener {
            firebaseAuth.signOut()
            Toast.makeText(activity, "Signed Out",Toast.LENGTH_LONG).show()
            activity?.let {
                val intent = Intent(it, MainActivity::class.java)
                it.startActivity(intent)
            }
        }
        val btnMap = view?.findViewById<Button>(R.id.btnMap)
        btnMap?.setOnClickListener {
            activity?.let {
                Log.i("status", "ok")
                val intent = Intent(it, MapActivity::class.java)
                it.startActivity(intent)
            }
        }
        val btnCurrencies = view?.findViewById<Button>(R.id.btnCurrenciesExchange)
        btnCurrencies?.setOnClickListener {
            activity?.let {
                val intent = Intent(it, MoneyExchangeActivity::class.java)
                it.startActivity(intent)
            }
        }

        val btnInterest = view?.findViewById<Button>(R.id.btnInterestRate)
        btnInterest?.setOnClickListener {
            activity?.let {
                val intent = Intent(it, InterestRateActivity::class.java)
                it.startActivity(intent)
            }
        }
        val btnPush = view?.findViewById<Button>(R.id.btnPush)
        btnPush?.setOnClickListener {
            activity?.let {
                val database = Firebase.database
                val myRef = database.getReference("message")
                myRef.setValue("Hello, World!")
                Log.i("status", "ok")
            }
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UserFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}