package com.nickjgski.vtjoinme

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {

    private lateinit var mFirebaseAnalytics: FirebaseAnalytics
    private lateinit var mAuth: FirebaseAuth
    companion object {
        val RC_SIGN_IN: Int = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        mAuth = FirebaseAuth.getInstance()
        if(mAuth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(
                        mutableListOf(AuthUI.IdpConfig.EmailBuilder().build())
                    ).build(), RC_SIGN_IN)
        }



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            var response: IdpResponse? = IdpResponse.fromResultIntent(data)

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                var user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                Snackbar.make(findViewById(R.id.content), "Sign in failed", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
    // [END auth_fui_result]

    fun signOut() {
        // [START auth_fui_signout]
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener(object: OnCompleteListener<Void> {
                override fun onComplete(p0: Task<Void>) {
                    startActivityForResult(AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(
                            mutableListOf(AuthUI.IdpConfig.EmailBuilder().build(),
                                AuthUI.IdpConfig.PhoneBuilder().build())
                        ).build(), RC_SIGN_IN)
                }
            })
        // [END auth_fui_signout]
    }

    fun delete() {
        // [START auth_fui_delete]
        AuthUI.getInstance()
            .delete(this)
            .addOnCompleteListener(object: OnCompleteListener<Void> {
                override fun onComplete(p0: Task<Void>) {

                }
            })
        // [END auth_fui_delete]
    }
}
