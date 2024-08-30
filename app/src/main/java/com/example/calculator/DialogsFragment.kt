package com.example.calculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment

class DialogsFragment: DialogFragment(){
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.information, container, false)
        val button = view.findViewById<Button>(R.id.ok)
        button.setOnClickListener {
            Toast.makeText(context, "OK", Toast.LENGTH_SHORT).show()
            dismiss() // Optional: close the dialog
        }
        return view
    }
    companion object {
        fun newInstance(): DialogsFragment {
            return DialogsFragment()
        }
    }
}