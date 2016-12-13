package com.qianxia.sijia.util;

import android.text.TextUtils;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tarena on 2016/10/26.
 */
public class EditInputUtil {

    public static boolean isEmpty(EditText... editTexts) {
        for (EditText et : editTexts) {
            if (TextUtils.isEmpty(et.getText().toString())) {
                et.setError("必填项，请输入！");
                return true;
            }
        }
        return false;
    }

    public static boolean hasSpechars(EditText... editTexts) {
        if (!isEmpty(editTexts)) {
            Pattern pattern = Pattern.compile("[0-9a-zA-Z\u4E00-\u9FFF]");
            for (EditText editText : editTexts) {
                String content = editText.getText().toString();
                Matcher matcher = pattern.matcher(content);
                if (!matcher.find()) {
                    editText.setError("请输入数字或者英文字母！");
                    return true;
                }
            }
        }
        return false;
    }

}
