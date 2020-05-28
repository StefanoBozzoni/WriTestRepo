package com.vjapp.writest

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.text.*
import android.text.style.RelativeSizeSpan
import android.util.Patterns
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import kotlinx.android.synthetic.main.login_layout.*
import org.koin.android.viewmodel.ext.android.viewModel


class LoginActivity : AppCompatActivity() {
    private var mAuthTask: UserLoginTask? = null
    private val loginViewModel: ViewModel by viewModel()

    private val isSignUpMode: Boolean
        get() {
            return confirm_password_form.visibility == View.VISIBLE
        }

    private val isSignInMode: Boolean
        get() {
            return !isSignUpMode
        }

    // UI references.
    private var mUserName: AutoCompleteTextView? = null
    private var mProgressView: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_form_login);
        setContentView(R.layout.activity_login_card_overlap)
        initComponent()
        hideSystemUI()
        tvPassword.setHintAndReduceFontSize(getString(R.string.inserisci_la_tua_password),0.9f)
        tvUserName.setHintAndReduceFontSize(getString(R.string.inserisci_la_tua_email),0.9f)
    }

    fun hideSystemUI() {
        if (supportActionBar != null) (supportActionBar as ActionBar).hide()
    }

    private fun initComponent() {
        // Set up the login form.
        mUserName = findViewById<View>(R.id.tvUserName) as AutoCompleteTextView
        tvPassword.setOnEditorActionListener(OnEditorActionListener { v, id, _ ->
            if (v.id == R.id.tvPassword || id == EditorInfo.IME_ACTION_DONE) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })

        val mTelephoneSignInButton =
            findViewById<View>(R.id.email_sign_in_button) as Button
        mTelephoneSignInButton.setOnClickListener { attemptLogin() }
        sign_up.setOnClickListener { toggleLoginRegistration() }


        tvPassword.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                //tilPassword.isEndIconVisible=true
                tilPassword.error = null
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
        })
        mProgressView = findViewById(R.id.login_progress)
    }

    private fun toggleLoginRegistration() {
        if (confirm_password_form.visibility != View.VISIBLE) {
            confirm_password_form.visibility = View.VISIBLE

            val strHint = getString(R.string.scegli_una_password)
            tvPassword.setHintAndReduceFontSize(strHint,0.9f)

            Toast.makeText(this, "registrazione (da implementare)", Toast.LENGTH_SHORT).show()
            sign_up.text = "accedi"
            lblPrimoAccesso.text = "ti sei già registrato?"
            email_sign_in_button.text = "REGISTRATI"
        } else {
            confirm_password_form.visibility = View.GONE
            val strHint = getString(R.string.scegli_una_password)
            tvPassword.setHintAndReduceFontSize(strHint,0.9f)

            sign_up.text = "registrati"
            lblPrimoAccesso.text = "primo accesso ?"
            email_sign_in_button.text = "ACCEDI"
        }
    }

    private fun attemptLogin() {

        if (mAuthTask != null) {
            return
        }

        tvUserName.error = null
        tvPassword.error = null
        tilPassword.error = null
        tvConfirmPassword.error = null

        val userName    = tvUserName.text.toString()
        val password    = tvPassword.text.toString().trim { it <= ' ' }
        val confirmPswd = tvConfirmPassword.text.toString().trim { it <= ' ' }

        if (isSignUpMode) {
            if (password != confirmPswd) {
                //tilPassword.isEndIconVisible=false
                tilPassword.error = "le password non coincidono, riprova"
                return
            }
            //
        } else {
            // Store values at the time of the login attempt.
            var cancel = false
            var focusView: View? = null

            // Check for a valid password, if the user entered one.
            if (!isPasswordValid(password)) {
                //tilPassword.isPasswordVisibilityToggleEnabled=false
                //tilPassword.isEndIconCheckable=false
                //tilPassword.setEndIconTintMode(PorterDuff.Mode.CLEAR)
                //tilPassword.isEndIconVisible=false
                //tilPassword.endIconMode=TextInputLayout.END_ICON_NONE
                //tilPassword.requestLayout()
                tilPassword.error = "Password non valida"
                focusView = tvPassword
                cancel = true
            }

            // Check for a valid telephone address.
            if (TextUtils.isEmpty(userName)) {
                tvUserName.error = "Email obbligatoria"
                focusView = tvUserName
                cancel = true
            } else if (!isEmailValid(userName)) {
                tvUserName.error = "Email non valida"
                focusView = tvUserName
                cancel = true
            }

            if (cancel) {
                // There was an error; don't attempt login and focus the first form field with an error.
                focusView!!.requestFocus()
            } else {
                // Show a progress spinner, and kick off a background task to perform the user login attempt.
                showProgress(true)
                mAuthTask = UserLoginTask(userName, password)
                mAuthTask!!.execute(null as Void?)
            }
        }
    }

    private fun isEmailValid(telephoneStr: String): Boolean {
        return !TextUtils.isEmpty(telephoneStr) && Patterns.EMAIL_ADDRESS.matcher(telephoneStr)
            .matches()
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 4
    }

    private fun showProgress(show: Boolean) {
        mProgressView!!.visibility = if (show) View.VISIBLE else View.GONE
        //tvUserName.visibility = if (show) View.GONE else View.VISIBLE
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    inner class UserLoginTask internal constructor(
        private val mUserName: String,
        private val mPassword: String
    ) : AsyncTask<Void?, Void?, Boolean>() {

        override fun doInBackground(vararg params: Void?): Boolean {
            // TODO: attempt authentication against a network service.
            try {
                // Simulate network access.
                Thread.sleep(2000)
            } catch (e: InterruptedException) {
                return false
            }
            for (credential in DUMMY_CREDENTIALS) {
                val pieces = credential.split(":").toTypedArray()
                if (pieces[0] == mUserName) {
                    // Account exists, return true if the password matches.
                    return pieces[1] == mPassword
                }
            }

            if (isSignUpMode) {
                // TODO: register the new account here.
            }

            return true
        }

        override fun onPostExecute(success: Boolean) {
            mAuthTask = null
            showProgress(false)
            if (success) {
                showProgress(false)
                Toast.makeText(applicationContext, "Accesso eseguito", Toast.LENGTH_SHORT).show()
                val i = Intent(applicationContext, UploadFilesActivity::class.java )
                startActivity(i)
            } else {
                tilPassword.error = "Password non valida"
                tvPassword.requestFocus()
            }
        }

        override fun onCancelled() {
            mAuthTask = null
            showProgress(false)
        }
    }

    companion object {
        private val DUMMY_CREDENTIALS = arrayOf(
            "foo@example.com:hello", "bar@example.com:world"
        )
    }

    fun TextView.setHintAndReduceFontSize(str:String, proportion: Float) {
        val span = SpannableString(str)
        span.setSpan(
            RelativeSizeSpan(proportion),
            0,
            str.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        this.setHint(span)
    }

}