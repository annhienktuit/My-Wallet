package com.annhienktuit.mywallet

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import com.annhienktuit.mywallet.fragments.UserFragment
import com.annhienktuit.mywallet.utils.Extensions.toast
import com.annhienktuit.mywallet.utils.FirebaseUtils.firebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.edtEmail
import kotlinx.android.synthetic.main.activity_login.edtPassword
import kotlinx.android.synthetic.main.activity_sign_up.*

class LoginActivity : AppCompatActivity() {
    lateinit var signInEmail: String
    lateinit var signInPassword: String
    lateinit var signInInputsArray: Array<EditText>
    var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+" //bieu thuc regex cho email format
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        if (getSupportActionBar() != null) {
            getSupportActionBar()?.hide();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(Color.parseColor("#FFFFFF"))
        }
        signInInputsArray = arrayOf(edtEmail, edtPassword)
        textViewDoSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }
        btnDoLogin.setOnClickListener {
            signIn()
        }
    }

    private fun notEmpty(): Boolean = signInEmail.isNotEmpty() && signInPassword.isNotEmpty()
    private fun isEmailFormat():Boolean = edtEmail.text.matches(emailPattern.toRegex())

    private fun signIn() {
        signInEmail = edtEmail.text.toString().trim()
        signInPassword = edtPassword.text.toString().trim()

        if (notEmpty() && isEmailFormat()) {
            firebaseAuth.signInWithEmailAndPassword(signInEmail, signInPassword)
                .addOnCompleteListener { signIn ->
                    if (signIn.isSuccessful) {
                        startActivity(Intent(this, MainActivity::class.java))
                        toast(getString(R.string.sign_in_success))
                        finish()
                    } else if(!isEmailFormat()) {
                        toast(getString(R.string.warning_email_format))
                    }
                    else {
                        toast(getString(R.string.sign_in_failed))
                    }
                }
        } else {
            signInInputsArray.forEach { input ->
                if (input.text.toString().trim().isEmpty()) {
                    input.error = "${input.hint} is required"
                }
            }
        }
    }
}