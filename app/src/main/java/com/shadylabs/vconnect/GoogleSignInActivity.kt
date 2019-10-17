package com.shadylabs.vconnect

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task


class GoogleSignInActivity : AppCompatActivity() {

    val context = this
    private val RC_SIGN_IN: Int = 111
    private var mGoogleSignInClient: GoogleSignInClient? = null
    lateinit var loginDetails: AppCompatTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_signin)


        // Set the dimensions of the sign-in button.
//        val signInButton = findViewById(R.id.sign_in_button)
//        signInButton.setOnClickListener {
//            signIn()
//        }

        loginDetails = findViewById(R.id.accountLog)


//        signInButton.setSize(SignInButton.SIZE_STANDARD)


        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)


        checkIfLoggedId(false)

    }

    private fun checkIfLoggedId(isToLogin: Boolean) {
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {
            updateUI(account)

        } else if (isToLogin) {
            ProcessSignIn()
        }
    }

    private fun updateUI(account: GoogleSignInAccount?) {
        if (account != null) {
            Toast.makeText(this, account.displayName, Toast.LENGTH_SHORT).show()
            loginDetails.text = "User Logged In : ${account.displayName}"
        } else {
            loginDetails.text = "User Logged Out"
        }
    }

    fun ProcessSignIn() {
        val signInIntent = mGoogleSignInClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun signIn(view: View?) {
        checkIfLoggedId(true)
    }

    fun signOut(view: View?) {
        logOut()
    }

    private fun logOut() {
        mGoogleSignInClient?.signOut()?.addOnCompleteListener(this, object : OnCompleteListener<Void> {
            override fun onComplete(task: Task<Void>) {

                Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                updateUI(null)
            }
        })
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }


    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            // Signed in successfully, show authenticated UI.
            updateUI(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("signInResult ", "signInResult:failed code=" + e.statusCode)
            updateUI(null)
        }

    }

}
