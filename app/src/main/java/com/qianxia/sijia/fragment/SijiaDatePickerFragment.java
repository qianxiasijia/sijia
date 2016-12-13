package com.qianxia.sijia.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Administrator on 2016/10/25.
 */
public class SijiaDatePickerFragment extends DialogFragment {

    //    private static SijiaDatePickerFragment datePickerFragment;
    private DatePickerDialog mDialog;
    private int year;
    private int month;
    private int day;
    private DatePickerDialog.OnDateSetListener listener;


//    public static SijiaDatePickerFragment getInstance(DatePickerDialog.OnDateSetListener listener){
//        if(datePickerFragment==null){
//            synchronized (SijiaDatePickerFragment.class){
//                if(datePickerFragment==null){
//                    datePickerFragment=new SijiaDatePickerFragment();
//                    datePickerFragment.listener=listener;
//                }
//            }
//        }
//        return  datePickerFragment;
//    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (DatePickerDialog.OnDateSetListener) activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance(Locale.CHINESE);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH) + 1;
        if (listener != null) {
            mDialog = new SijiaDatePickerDialog(getActivity(), listener, year, month, day);
        }
        mDialog.show();
        return mDialog;
    }

    class SijiaDatePickerDialog extends DatePickerDialog {

        public SijiaDatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
            super(context, callBack, year, monthOfYear, dayOfMonth);
        }

        public SijiaDatePickerDialog(Context context, int theme, OnDateSetListener listener, int year, int monthOfYear, int dayOfMonth) {
            super(context, theme, listener, year, monthOfYear, dayOfMonth);
        }

        @Override
        protected void onStop() {
//            super.onStop();
        }
    }
}
