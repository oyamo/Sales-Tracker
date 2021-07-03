package com.oyasis.salestracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.oyasis.salestracker.R
import com.oyasis.salestracker.databinding.FragmentErrorBinding

class ErrorAlert: DialogFragment() {
    private lateinit var binding: FragmentErrorBinding
    var errorText: String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentErrorBinding.inflate(inflater)
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireActivity())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        errorText?.let {
            binding.textDescription.text = it
        }
        binding.closeBtn.setOnClickListener {
            dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme_transparent);
    }
}