package com.qianxia.sijia.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

/**
 * Created by tarena on 2016/10/17.
 */
public class PinYinUtil {

    /**
     * 将汉字转换成拼音
     *
     * @param name
     * @return
     */
    public static String getPinYin(String name) {
        try {
            String result = "";
            //利用pinyin4j.jar进行汉字-->拼音的转换

            //设定拼音的格式
            HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();

            //拼音字母全大写
            format.setCaseType(HanyuPinyinCaseType.UPPERCASE);

            //拼音不需要音调
            format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

            //按照格式进行转换
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < name.length(); i++) {
                String str = name.substring(i, i + 1);
                if (str.matches("[\u4e00-\u9fff]")) {
                    //str时汉字的话
                    char c = name.charAt(i);
                    //因为有多音字，所以返回值为数组形式
                    String[] py = PinyinHelper.toHanyuPinyinStringArray(c, format);
                    //只取多个读音中的第一种读音
                    builder.append(py[0]);
                } else if (str.matches("[a-zA-Z]")) {
                    //str是英文字母的话
                    builder.append(str.toUpperCase());
                } else {
                    //str既不是汉字也不是英文字母
                    if (i == 0) {
                        //如果是首位
                        builder.append("#");
                    } else {
                        builder.append(str);
                    }
                }
            }
            result = builder.toString();
            return result;
        } catch (Exception e) {
            throw new RuntimeException("没有");
        }
    }


}
