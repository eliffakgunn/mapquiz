package com.example.mapquiz

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment


class ConfirmPasswordDialog : DialogFragment(){
    interface OnConfirmPasswordListener{
        fun onConfirmPassword(password: String)
    }

    var mOnConfirmPasswordListener : OnConfirmPasswordListener? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_confirm_password, container,false)
        val dialogCancel = view.findViewById<TextView>(R.id.dialogCancel)
        val dialogConfirm = view.findViewById<TextView>(R.id.dialogConfirm)
        val confirm_password = view.findViewById<EditText>(R.id.confirm_password)


        dialogCancel.setOnClickListener {
            dialog?.dismiss()
        }

        dialogConfirm.setOnClickListener{
            val password = confirm_password.text.toString().trim { it <= ' ' }

            mOnConfirmPasswordListener?.onConfirmPassword(password)
            dialog?.dismiss()
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try{
            mOnConfirmPasswordListener = targetFragment as OnConfirmPasswordListener
        }catch (e: ClassCastException){
            Log.e("ConfirmPasswordDialog", "onAttach ClassCastException: " + e.message)
        }
    }
}