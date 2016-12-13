package com.qianxia.sijia.util;

import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.TypedValue;

import com.qianxia.sijia.application.SijiaApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/11/10.
 */
public class EmoUtil {

    public static List<String> emos = new ArrayList<>();

    static {
        for (int i = 0; i <= 41; i++) {
            String str = null;
            if (i < 10) {
                str = "[ue00" + i + "]";
            } else {
                str = "[ue0" + i + "]";
            }
            emos.add(str);
        }
    }

    public static SpannableString getSpannableString(String string) {
        SpannableString ss = new SpannableString(string);

        Pattern pattern = Pattern.compile("\\[ue[0-9a-z]{3}\\]");
        Matcher matcher = pattern.matcher(ss);
        while (matcher.find()) {
            String str = matcher.group();
            String resName = str.substring(1, str.length() - 1);
            int resId = SijiaApplication.context.getResources().getIdentifier(resName, "drawable", SijiaApplication.context.getPackageName());
            if (resId != 0) {
                int startIdx = matcher.start();
                int endIdx = matcher.end();
                Drawable drawable = SijiaApplication.context.getResources().getDrawable(resId);
                int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, SijiaApplication.context.getResources().getDisplayMetrics());
                drawable.setBounds(0, 0, size, size);
                ImageSpan span = new ImageSpan(drawable);
                ss.setSpan(span, startIdx, endIdx, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
//
//        Pattern pattern = Pattern.compile("ue[0-9a-z]{3}");
//        Matcher matcher = pattern.matcher(ss);
//        while(matcher.find()){
//            String resName = matcher.group();//ue056
//            int resId = MyApp.context.getResources().getIdentifier(resName, "drawable", MyApp.context.getPackageName());
//            if(resId!=0){
//                //R.drawable.ue056
//                int startIdx = matcher.start();
//                int endIdx = matcher.end();
//                ss.setSpan(new ImageSpan(MyApp.context, resId), startIdx, endIdx, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
//            }
//        }

        return ss;
    }

}
