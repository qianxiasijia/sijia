package com.qianxia.sijia.util;

import android.provider.ContactsContract;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2016/9/16.
 */
public class FormatDataUtil {

    public static String formatData(Long date) {
        String resDate = null;
        long dayoff = (System.currentTimeMillis() - date) / 1000 / 60 / 60 / 24;
        if (dayoff > 3) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            resDate = format.format(new Date(date));
        } else {
            Calendar calendarSend = Calendar.getInstance();
            Calendar calendarCurrent = Calendar.getInstance();
            calendarSend.setTimeInMillis(date);
            int tempDate = calendarCurrent.get(Calendar.DAY_OF_YEAR) - calendarSend.get(Calendar.DAY_OF_YEAR);
            if (tempDate <= 1) {
                SimpleDateFormat format1 = new SimpleDateFormat("HH:mm");
                if (tempDate == 0) {
                    resDate = format1.format(new Date(date));
                } else {
                    resDate = "昨天" + " " + format1.format(new Date(date));
                }
            } else {
                SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                resDate = format2.format(new Date(date));
            }
        }
        return resDate;
    }
}
