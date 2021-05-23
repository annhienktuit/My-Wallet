package com.annhienktuit.mywallet

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import com.annhienktuit.mywallet.utils.Extensions.toast
import com.annhienktuit.mywallet.utils.FirebaseInstance
import com.annhienktuit.mywallet.utils.FirebaseUtils
import com.annhienktuit.mywallet.utils.FirebaseUtils.firebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.edtPassword
import kotlinx.android.synthetic.main.activity_login.edtEmail
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.fragment_user.*
import java.security.MessageDigest

class SignUpActivity : AppCompatActivity() {
    lateinit var userEmail: String
    lateinit var userPassword: String
    lateinit var userName: String
    lateinit var userUID: String
    lateinit var createAccountInputsArray: Array<EditText>
    var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+" //bieu thuc regex cho email format
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        if (getSupportActionBar() != null) {
            getSupportActionBar()?.hide();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(Color.parseColor("#FFFFFF"))
        }
        // Innitialize information
        createAccountInputsArray = arrayOf(edtEmail, edtPassword, edtConfirmPassword, edtFirstName, edtLastName)
        btnDoSignUp.setOnClickListener {
            signIn()
        }
        textviewDoSignIn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
    override fun onStart() {
        super.onStart()
        val user: FirebaseUser? = firebaseAuth.currentUser
        if(user !== null) {
            toast("Already Logged In")
            startActivity(Intent(this, MainActivity::class.java))
        }
        hash("annhienkt")
    }
    fun hash(pw: String): String {
        val bytes = this.toString().toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        var result = digest.fold(pw, { str, it -> str + "%02x".format(it) })
        Log.i("currentPass", pw)
        result = result.replace("$pw","")
        Log.i("hashedPass", result)
        return result
    }

    private fun notEmpty(): Boolean = edtEmail.text.toString().trim().isNotEmpty() &&
            edtPassword.text.toString().trim().isNotEmpty() &&
            edtConfirmPassword.text.toString().trim().isNotEmpty()
    // skip for easier testing
//            &&
//            edtFirstName.text.toString().trim().isNotEmpty() &&
//            edtLastName.text.toString().trim().isNotEmpty()

    private fun isEmailFormat():Boolean = edtEmail.text.matches(emailPattern.toRegex())

    private fun identicalPassword(): Boolean {
        var identical = false
        if (notEmpty() &&
            edtPassword.text.toString().trim() == edtConfirmPassword.text.toString().trim() && isEmailFormat()
        ) {
            identical = true
        } else if (!notEmpty()) {
            createAccountInputsArray.forEach { input ->
                if (input.text.toString().trim().isEmpty()) {
                    input.error = "${input.hint} is required"
                }
            }

        }
        else if(!isEmailFormat()) {
            toast(getString(R.string.warning_email_format))
        } else {
            toast(getString(R.string.warning_passwords_matching))
        }
        return identical
    }

    private fun signIn() {
        if (identicalPassword()) {
            userEmail = edtEmail.text.toString().trim()
            userPassword = edtPassword.text.toString().trim()
            userName = (edtFirstName.text.toString() + edtLastName.text.toString()).trim()
            firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        toast(getString(R.string.account_created))
                        pushToFireBase(userName,userEmail,userPassword)
                        val intentMain = Intent(this, MainActivity::class.java)
                        intentMain.putExtra("Full Name",createAccountInputsArray[3].text.toString() + " " +createAccountInputsArray[4].text.toString())
                        startActivity(intentMain)
                        finish()
                    } else {
                        toast(getString(R.string.authentication_failed))
                    }
                }
        }
    }

    private fun pushToFireBase(name: String, email: String, password: String){
        val user: FirebaseUser? = FirebaseUtils.firebaseAuth.currentUser
        val uid = user!!.uid
        var hashedPassword = hash(password)
        val database = FirebaseDatabase.getInstance(FirebaseInstance.INSTANCE_URL)
        var myRef = database.getReference("users").child(uid).child("name").setValue(name)
        myRef = database.getReference("users").child(uid).child("email").setValue(email)
        myRef = database.getReference("users").child(uid).child("password").setValue(hashedPassword)


    }

}