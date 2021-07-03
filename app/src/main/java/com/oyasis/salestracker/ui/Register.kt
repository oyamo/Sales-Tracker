package com.oyasis.salestracker.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.vvalidator.form
import com.oyasis.salestracker.Cognito
import com.oyasis.salestracker.databinding.ActivityRegisterBinding

class Register : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var cognito: Cognito
    private lateinit var progress: LoadingFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cognito = Cognito(this)
        progress = LoadingFragment()


        form {
            input(binding.editFullnames, "Full names") {
                isNotEmpty()
                length().lessThan(100)
            }

            input(binding.editAddress, "Physical address") {
                isNotEmpty()
                length().lessThan(100)
            }

            input(binding.editEmail, "Email address") {
                isEmail()
            }

            input(binding.editPassword, "Password") {
                length().atLeast(8)
            }
            submitWith(binding.signUpBtn) { formResult ->
                if (formResult.success()) {
                    val cognito = Cognito(this@Register)
                    val username = binding.editEmail.text.toString()
                    val fullnames = binding.editFullnames.text.toString()
                    val password = binding.editPassword.text.toString()
                    val address = binding.editAddress.text.toString()

                    cognito.addAttribute("address", address)
                    cognito.addAttribute("name", fullnames)
                    cognito.addAttribute("email", username)

                    toggleProgress()
                    cognito.signUpInBackground(username, password) { user, state, e ->
                        toggleProgress()
                        user?.let {

                            val intent  = Intent(this@Register, ConfirmCode::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.putExtra("userId", username)
                            startActivity(intent)


                        }
                        e?.let {
                           showError(err = it.localizedMessage?: "Could not sign up")
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




}
