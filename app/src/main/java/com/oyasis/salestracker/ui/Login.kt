package com.oyasis.salestracker.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.afollestad.vvalidator.form
import com.oyasis.salestracker.Cognito
import com.oyasis.salestracker.databinding.ActivityLoginBinding

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var cognito: Cognito
    private lateinit var progress: LoadingFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)
        cognito = Cognito(this )
        progress = LoadingFragment()

        form {
            input(binding.editUsername, "Username") {
                isEmail()
                isNotEmpty()
            }

            input(binding.editPassword, "Password") {
                isNotEmpty()
            }

            submitWith(binding.loginBtn) { formResult ->
                toggleProgress()
                if(formResult.success()) {
                    cognito.userLogin(
                        binding.editUsername.text.toString(),
                        binding.editPassword.text.toString()
                    ) {
                        userSession, exception ->

                            toggleProgress()

                            exception?.let {
                                showError(it.message!!)
                            }
                            userSession?.let {
                                val pref = UserPreferencesRepository.getInstance(this@Login)
                                pref.updateKey(it.accessToken.jwtToken)

                               val intent = Intent(this@Login, ActivityDashBoard::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                startActivity(intent)
                                finish()
                            }
                    }
                }
            }
        }
    }

    fun toggleProgress() {
        if(progress.isVisible) {
            progress.dismiss()
        } else {
            progress.show(supportFragmentManager, null)
        }
    }

    fun showError(err: String) {
        val fm = supportFragmentManager
        val edlg = ErrorAlert()
        edlg.errorText = err
        edlg.show(fm, null);
    }



    fun showSuccess() {
        val fragmentManager =supportFragmentManager
        val dlg  = AlertFragment()
        dlg.show(fragmentManager, null)
    }
}