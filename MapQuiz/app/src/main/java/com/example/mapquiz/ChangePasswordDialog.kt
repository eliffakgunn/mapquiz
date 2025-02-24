package com.example.mapquiz

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment

class ChangePasswordDialog : DialogFragment() {
    interface OnChangePasswordListener {
        fun onChangePassword(curr_password: String, new_password1: String, new_password2: String)
    }

    var mOnChangePasswordListener : ChangePasswordDialog.OnChangePasswordListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_change_password, container, false)

        val dialogCancel = view.findViewById<TextView>(R.id.dialogCancel)
        val dialogConfirm = view.findViewById<TextView>(R.id.dialogConfirm)
        val current_password = view.findViewById<EditText>(R.id.current_password)
        val new_password1 = view.findViewById<EditText>(R.id.new_password)
        val new_password2 = view.findViewById<EditText>(R.id.new_password2)

        dialogCancel.setOnClickListener {
            dialog?.dismiss()
        }

        dialogConfirm.setOnClickListener{
            val current_pass = current_password.text.toString().trim { it <= ' ' }
            val new_pass1 = new_password1.text.toString().trim { it <= ' ' }
            val new_pass2 = new_password2.text.toString().trim { it <= ' ' }

            mOnChangePasswordListener?.onChangePassword(current_pass, new_pass1, new_pass2)
            dialog?.dismiss()
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try{
            mOnChangePasswordListener = targetFragment as ChangePasswordDialog.OnChangePasswordListener
        }catch (e: ClassCastException){
            Log.e("ChangePasswordDialog", "onAttach ClassCastException: " + e.message)
        }
    }

}

