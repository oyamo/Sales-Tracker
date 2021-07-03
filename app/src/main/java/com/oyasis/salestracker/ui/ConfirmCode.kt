package com.oyasis.salestracker.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import com.oyasis.salestracker.Cognito
import com.oyasis.salestracker.R
import com.oyasis.salestracker.databinding.ActivityConfirmCodeBinding

class ConfirmCode : AppCompatActivity() {
    private lateinit var binding: ActivityConfirmCodeBinding
    private lateinit var cognito: Cognito
    private var email: String? = null
    private lateinit var progress: LoadingFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras = intent.extras
        progress = LoadingFragment()
        email = extras?.getString("userId")
        cognito = Cognito(this)
        email?.let {

            binding.resendBtn.setOnClickListener {
                toggleProgress()
                cognito.reSendConfirmationCode(email!!) {
                    vmedium, exception ->

                    toggleProgress()
                   vmedium?.let {
                       showSuccess()
                   }

                    exception?.let {
                        showError(it.localizedMessage?: "Could not get verification code")
                    }

                }
            }
        }

        setUpForm()

    }



    fun setUpForm() {
        binding.editText1.addTextChangedListener(GenericTextWatcher( binding.editText1,  binding.editText2))
        binding.editText2.addTextChangedListener(GenericTextWatcher( binding.editText2,  binding.editText3))
        binding.editText3.addTextChangedListener(GenericTextWatcher( binding.editText3,  binding.editText4))
        binding.editText4.addTextChangedListener(GenericTextWatcher( binding.editText4,  binding.editText5))
        binding.editText5.addTextChangedListener(GenericTextWatcher( binding.editText5,  binding.editText6))
        binding.editText6.addTextChangedListener(GenericTextWatcher( binding.editText6,  null))

        binding.editText1.setOnKeyListener(GenericKeyEvent( binding.editText1, null))
        binding.editText2.setOnKeyListener(GenericKeyEvent( binding.editText2,  binding.editText1))
        binding.editText3.setOnKeyListener(GenericKeyEvent( binding.editText3,  binding.editText2))
        binding.editText4.setOnKeyListener(GenericKeyEvent( binding.editText4, binding.editText3))
        binding.editText5.setOnKeyListener(GenericKeyEvent( binding.editText5, binding.editText4))
        binding.editText6.setOnKeyListener(GenericKeyEvent( binding.editText6, binding.editText5))
    }

    private fun showSuccess() {
        val fragmentManager =supportFragmentManager
        val dlg  = AlertFragment()
        dlg.show(fragmentManager, null)
    }

    fun showError(err: String) {
        val fm = supportFragmentManager
        val edlg = ErrorAlert()
        edlg.errorText = err
        edlg.show(fm, null);
    }

    fun toggleProgress() {
        if(progress.isVisible) {
            progress.dismiss()
        } else {
            progress.show(supportFragmentManager, null)
        }
    }


    class GenericKeyEvent internal constructor(private val currentView: EditText, private val previousView: EditText?) : View.OnKeyListener{
        override fun onKey(p0: View?, keyCode: Int, event: KeyEvent?): Boolean {
            if(event!!.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && currentView.id != R.id.editText1 && currentView.text.isEmpty()) {
                //If current is empty then previous EditText's number will also be deleted
                previousView!!.text = null
                previousView.requestFocus()
                return true
            }
            return false
        }


    }

    inner class GenericTextWatcher internal constructor(private val currentView: View, private val nextView: View?) :
        TextWatcher {
        override fun afterTextChanged(editable: Editable) {
            val text = editable.toString()
            when (currentView.id) {
                R.id.editText1 -> if (text.length == 1) nextView!!.requestFocus()
                R.id.editText2 -> if (text.length == 1) nextView!!.requestFocus()
                R.id.editText3 -> if (text.length == 1) nextView!!.requestFocus()
                R.id.editText4 -> if (text.length == 1) nextView!!.requestFocus()
                R.id.editText5 -> if (text.length == 1) nextView!!.requestFocus()
                R.id.editText6 -> {
                    val values = arrayOf(
                        binding.editText1.text.toString(),
                        binding.editText2.text.toString(),
                        binding.editText3.text.toString(),
                        binding.editText4.text.toString(),
                        binding.editText5.text.toString(),
                        binding.editText6.text.toString(),
                    )

                    val code = values.joinToString(
                        separator = "",
                        prefix = "",
                        postfix=""
                    )

                    toggleProgress()
                    cognito.confirmUser(email!!, code) {
                        toggleProgress()
                        if(it == null) {
                            val intent = Intent(this@ConfirmCode, ActivityDashBoard::class.java)
                            startActivity(intent)
                            finish()
                        }

                        it?.let {
                            showError(it.localizedMessage?:"Incorrect code")
                        }
                    }

                }
            }
        }

        override fun beforeTextChanged(
            arg0: CharSequence,
            arg1: Int,
            arg2: Int,
            arg3: Int
        ) {
        }

        override fun onTextChanged(
            arg0: CharSequence,
            arg1: Int,
            arg2: Int,
            arg3: Int
        ) {
        }

    }

}