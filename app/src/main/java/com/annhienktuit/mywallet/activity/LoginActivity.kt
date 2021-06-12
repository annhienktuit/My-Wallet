package com.annhienktuit.mywallet.activity

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import com.annhienktuit.mywallet.R
import com.annhienktuit.mywallet.utils.Extensions.toast
import com.annhienktuit.mywallet.utils.FirebaseUtils.firebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.edtEmail
import kotlinx.android.synthetic.main.activity_login.edtPassword
import kotlinx.android.synthetic.main.activity_sign_up.*

class LoginActivity : AppCompatActivity() {
    lateinit var signInEmail: String
    lateinit var signInPassword: String
    lateinit var signInInputsArray: Array<EditText>
    lateinit var auth: FirebaseAuth
    val TAG = "GoogleSignIn"
    lateinit var googleSignInClient: GoogleSignInClient
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
        //GMAILLOGIN
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)
        auth = FirebaseAuth.getInstance()
        btnLoginGoogle.setOnClickListener {
            signInGoogle()
        }
        signInInputsArray = arrayOf(edtEmail, edtPassword)
        textViewDoSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }
        btnDoLogin.setOnClickListener {
            signIn()
        }
        btnReset.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("RESET PASSWORD")
            val textEditor = EditText(this)
            textEditor.setHint("Enter your email address")
            builder.setView(textEditor)
            textEditor.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            builder.setPositiveButton("Confirm") { dialog, which ->
                sendResetEmail(textEditor.text.toString())
            }

            builder.show()
        }
    }

    private fun sendResetEmail(emailAddress:String) {
        Firebase.auth.sendPasswordResetEmail(emailAddress)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this,"Email sent to $emailAddress",Toast.LENGTH_LONG).show()
                    Log.d(TAG, "Email sent to $emailAddress")
                }
            }
    }

    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, 1)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 1) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if (task.isSuccessful) {
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d(TAG, "firebaseAuthWithGoogle:" + account.email)
                    firebaseAuthWithGoogle(account.idToken!!, account.displayName.toString())
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w(TAG, "Google sign in failed", e)
                }
            }

        }
    }

    private fun firebaseAuthWithGoogle(idToken: String, name: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    var ref = FirebaseDatabase
                        .getInstance("https://my-wallet-80ed7-default-rtdb.asia-southeast1.firebasedatabase.app/")
                        .getReference("datas")
                    ref.child(user!!.uid).child("name").setValue(name)
                    ref.child(user.uid).child("limits").child("total").setValue(0)
                    ref.child(user.uid).child("savings").child("total").setValue(0)
                    ref.child(user.uid).child("cards").child("total").setValue(0)
                    ref.child(user.uid).child("balance").setValue("0")
                    ref.child(user.uid).child("income").setValue("0")
                    ref.child(user.uid).child("expense").setValue("0")
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }

    override fun onStart() {
        super.onStart()
        val user: FirebaseUser? = firebaseAuth.currentUser
        if(user !== null) {
            toast("Already Logged In")
            startActivity(Intent(this, MainActivity::class.java))
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