package com.qianxia.sijia.fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.qianxia.sijia.R;
import com.qianxia.sijia.listener.OnDialogClickListener;

/**
 * Created by Administrator on 2016/11/11.
 */
public class DeleteDialogFrament extends DialogFragment implements DialogInterface.OnClickListener {

    private OnDialogClickListener listener;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("删除确认");
        builder.setIcon(R.drawable.ic_info);
        builder.setMessage("确定要删除吗？");
        builder.setNegativeButton(R.string.dialog_cancell, this);
        builder.setPositiveButton(R.string.dialog_sumbit, this);
        return builder.create();
    }

    public void setOnDialogClickListener(OnDialogClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case Dialog.BUTTON_NEGATIVE:
                if (listener != null) {
                    listener.onDialogNegativeClick(this);
                }
                break;
            case Dialog.BUTTON_POSITIVE:
                if (listener != null) {
                    listener.onDialogPositiveClick(this);
                }
                break;
        }

    }

}
