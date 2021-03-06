package com.annhienktuit.mywallet.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.annhienktuit.mywallet.R
import com.annhienktuit.mywallet.utils.FirebaseUtils
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_user.*


class UserActivity : AppCompatActivity() {
    val user: FirebaseUser? = FirebaseUtils.firebaseAuth.currentUser
    val ref = FirebaseDatabase
        .getInstance("https://my-wallet-80ed7-default-rtdb.asia-southeast1.firebasedatabase.app/")
        .getReference("datas").child(user!!.uid)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        //Get database
        getDatabase(ref, object : OnGetDataListener {
            @SuppressLint("SetTextI18n")
            override fun onSuccess(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.hasChild("name")) btnName.setText("Not set yet")
                else btnName.setText(dataSnapshot.child("name").value.toString())
                if (!dataSnapshot.hasChild("phone")) btnPhone.text = "Not set yet"
                else btnPhone.text = dataSnapshot.child("phone").value.toString()
                if (!dataSnapshot.hasChild("gender")) btnGender.text = "Not set yet"
                else btnGender.text = dataSnapshot.child("gender").value.toString()
                if (!dataSnapshot.hasChild("dob")) btnDOB.text = "Not set yet"
                else btnDOB.text = dataSnapshot.child("dob").value.toString()
                if (!dataSnapshot.hasChild("occupation")) btnOccupation.text = "Not set yet"
                else btnOccupation.text = dataSnapshot.child("occupation").value.toString()
            }
            override fun onStart() {
            }
            override fun onFailure() {
            }
        })
        btnShare.setOnClickListener {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            val shareBody = "Love this money management app, try it now"
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "My Wallet")
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
            startActivity(Intent.createChooser(sharingIntent, "Share via"))
        }
        btnDevTeam.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
        }
    }
    interface OnGetDataListener {
        fun onSuccess(dataSnapshot: DataSnapshot)
        fun onStart()
        fun onFailure()
    }
    fun getDatabase(ref: DatabaseReference, listener: OnGetDataListener?) {
        listener?.onStart()
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listener?.onSuccess(snapshot)
            }
            override fun onCancelled(error: DatabaseError) {
                listener?.onFailure()
            }
        })
    }
}