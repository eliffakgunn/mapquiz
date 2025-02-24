package com.example.mapquiz


import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment


class ResetPasswordDialog: AppCompatDialogFragment() {
    private var listener: ResetPasswordDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //return super.onCreateDialog(savedInstanceState)
        println("onCreateDialog")

        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)

        val inflater = activity!!.layoutInflater
        val view: View = inflater.inflate(R.layout.dialog_reset_password, null)

        val dialogCancel = view.findViewById<TextView>(R.id.dialogCancel)
        val dialogConfirm = view.findViewById<TextView>(R.id.dialogConfirm)
        val etEmail = view.findViewById<EditText>(R.id.reset_password)

        builder.setView(view)

        dialogCancel.setOnClickListener {
            dialog?.dismiss()
        }

        dialogConfirm.setOnClickListener {
            val email = etEmail.text.toString().trim { it <= ' ' }
            listener?.onResetPassword(email)
            dialog?.dismiss()
        }
        
        return builder.create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        println("onAttach")

           try {
            listener = context as ResetPasswordDialogListener;
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() +
                    "must implement ResetPasswordDialogListener");
        }
    }

    interface ResetPasswordDialogListener {
        fun onResetPassword(email: String)
    }
}