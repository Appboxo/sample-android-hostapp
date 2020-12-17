package com.appboxo.sample.hostapp

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.dialog_signtel_card.*


class CardDialog(context: Context) :
    BottomSheetDialog(context, com.appboxo.appboxo.R.style.Appboxo_BottomSheetDialog) {

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.dialog_signtel_card)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        window?.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        dismiss_view.setOnClickListener { cancel() }
        card_number.setOnClickListener {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("card_number", "4545 4545 4545 4545"))
            dismiss()
        }
    }

}