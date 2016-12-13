package cn.bmob.im.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

/**
 * JSON工具类：封装了Json字符串中各类型的读取操作
 *
 * @author smile
 * @ClassName: JSONUtil
 * @Description: TODO
 * @date 2014-6-4 下午3:05:50
 */
public class BmobJsonUtil {

    /**
     * 获取 string, key可用点用分割 ,空返回null
     *
     * @param @param  jo
     * @param @param  key
     * @param @return
     * @return String
     * @throws
     * @Title: getString
     * @Description: TODO
     */
    public static String getString(JSONObject jo, String key) {
        if (jo == null) {
            return null;
        }
        try {
            if (key.contains(".")) {
                String[] tks = key.split("\\.");
                JSONObject jot = jo;
                String value = null;
                for (int j = 0; j < tks.length; j++) {
                    String tk = tks[j];
                    if (jot.has(tk)) {
                        if (j == tks.length - 1) {
                            value = jot.getString(tk);
                        } else {
                            jot = jot.getJSONObject(tk);
                        }
                    } else {
                        if (value == null)
                            value = "";
                        return value;
                    }
                }
                if ("null".equals(value)) {
                    value = "";
                }
                return value;
            } else {
                String value = null;
                if (jo.has(key)) {
                    value = jo.getString(key);
                }
                if ("null".equals(value)) {
                    value = "";
                }
                return value;
            }
        } catch (JSONException e) {
        }
        return "";
    }

    /**
     * 获取string key可用点做分割 空时返回""
     *
     * @param @param  jo
     * @param @param  key
     * @param @return
     * @return String
     * @throws
     * @Title: getStringNoEmpty
     * @Description: TODO
     */
    public static String getStringNoEmpty(JSONObject jo, String key) {
        return getString(jo, key, "");
    }

    /**
     * getString
     *
     * @param @param  jo
     * @param @param  key
     * @param @param  def
     * @param @return
     * @return String
     * @throws
     * @Title: getString
     * @Description: TODO
     */
    public static String getString(JSONObject jo, String key, String def) {
        String value = getString(jo, key);
        if (!TextUtils.isEmpty(value)) {
            return value;
        } else {
            return def;
        }
    }

    /**
     * getInt
     *
     * @param @param  jo
     * @param @param  key
     * @param @return
     * @return Integer
     * @throws
     * @Title: getInt
     * @Description: TODO
     */
    public static Integer getInt(JSONObject jo, String key) {
        String value = getString(jo, key);
        if (!TextUtils.isEmpty(value)) {
            try {
                return Integer.parseInt(value);
            } catch (Exception e) {
            }
        }
        return 0;
    }

    /**
     * getInt
     *
     * @param @param  jo
     * @param @param  key
     * @param @param  def
     * @param @return
     * @return Integer
     * @throws
     * @Title: getInt
     * @Description: TODO
     */
    public static Integer getInt(JSONObject jo, String key, Integer def) {
        String value = getString(jo, key);
        if (!TextUtils.isEmpty(value)) {
            try {
                return Integer.parseInt(value);
            } catch (Exception e) {
            }

        }
        return def;
    }

    /**
     * getLong
     *
     * @param @param  jo
     * @param @param  key
     * @param @return
     * @return Long
     * @throws
     * @Title: getLong
     * @Description: TODO
     */
    public static Long getLong(JSONObject jo, String key) {
        String value = getString(jo, key);
        if (!TextUtils.isEmpty(value)) {
            try {
                return Long.parseLong(value);
            } catch (Exception e) {
            }

        }
        return 0l;
    }

    /**
     * getLong
     *
     * @param @param  jo
     * @param @param  key
     * @param @param  def
     * @param @return
     * @return Long
     * @throws
     * @Title: getLong
     * @Description: TODO
     */
    public static Long getLong(JSONObject jo, String key, Long def) {
        String value = getString(jo, key);
        if (!TextUtils.isEmpty(value)) {
            try {
                return Long.parseLong(value);
            } catch (Exception e) {
            }
        }
        return def;
    }

    /**
     * getFloat
     *
     * @param @param  jo
     * @param @param  key
     * @param @return
     * @return Float
     * @throws
     * @Title: getFloat
     * @Description: TODO
     */
    public static Float getFloat(JSONObject jo, String key) {
        String value = getString(jo, key);
        if (!TextUtils.isEmpty(value)) {
            try {
                return Float.parseFloat(value);
            } catch (Exception e) {
            }
        }
        return 0f;
    }

    /**
     * getDouble
     *
     * @param @param  jo
     * @param @param  key
     * @param @return
     * @return Double
     * @throws
     * @Title: getDouble
     * @Description: TODO
     */
    public static Double getDouble(JSONObject jo, String key) {
        String value = getString(jo, key);
        if (!TextUtils.isEmpty(value)) {
            try {
                return Double.parseDouble(value);
            } catch (Exception e) {
            }

        }
        return 0d;
    }

    /**
     * 获取boolean 如果没有返回 false
     * getBoolean
     *
     * @param @param  jo
     * @param @param  key
     * @param @return
     * @return Boolean
     * @throws
     * @Title: getBoolean
     * @Description: TODO
     */
    public static Boolean getBoolean(JSONObject jo, String key) {
        String value = getString(jo, key);
        if (!TextUtils.isEmpty(value)) {
            try {
                if (value.equals("1")) {
                    return true;
                } else if (value.equals("0")) {
                    return false;
                }
                return Boolean.parseBoolean(value);
            } catch (Exception e) {
            }
        }
        return false;
    }

    /**
     * 获取jsonobject key 不可点分割
     * getJSONObject
     *
     * @param @param  jo
     * @param @param  key
     * @param @return
     * @return JSONObject
     * @throws
     * @Title: getJSONObject
     * @Description: TODO
     */
    public static JSONObject getJSONObject(JSONObject jo, String key) {
        if (jo == null || TextUtils.isEmpty(key))
            return null;
        try {
            if (key.contains(".")) {
                String[] tks = key.split("\\.");
                JSONObject jot = jo;
                JSONObject value = null;
                for (int j = 0; j < tks.length; j++) {
                    String tk = tks[j];
                    if (jot.has(tk)) {
                        if (j == tks.length - 1) {
                            value = jot.getJSONObject(tk);
                        } else {
                            jot = jot.getJSONObject(tk);
                        }
                    } else {
                        return null;
                    }
                }
                return value;
            } else {
                JSONObject value = null;
                if (jo.has(key)) {
                    value = jo.getJSONObject(key);
                }
                if ("null".equals(value)) {
                    value = null;
                }
                return value;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取jsonarray 可不可点分割
     * getJSONArray
     *
     * @param @param  jo
     * @param @param  key
     * @param @return
     * @return JSONArray
     * @throws
     * @Title: getJSONArray
     * @Description: TODO
     */
    public static JSONArray getJSONArray(JSONObject jo, String key) {
        if (jo == null || TextUtils.isEmpty(key))
            return null;
        try {
            if (key.contains(".")) {
                String[] tks = key.split("\\.");
                JSONObject jot = jo;
                JSONArray value = null;
                for (int j = 0; j < tks.length; j++) {
                    String tk = tks[j];
                    if (jot.has(tk)) {
                        if (j == tks.length - 1) {
                            value = jot.getJSONArray(tk);
                        } else {
                            jot = jot.getJSONObject(tk);
                        }
                    } else {
                        return null;
                    }
                }
                if (value == null) {
                    value = new JSONArray();
                }
                return value;
            } else {
                JSONArray value = null;
                if (jo.has(key)) {

                    value = jo.getJSONArray(key);
                }
                if ("null".equals(value)) {
                    value = null;
                }
                if (value == null) {
                    value = new JSONArray();
                }
                return value;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * getJSONObjectAt
     *
     * @param @param  array
     * @param @param  index
     * @param @return
     * @return JSONObject
     * @throws
     * @Title: getJSONObjectAt
     * @Description: TODO
     */
    public static JSONObject getJSONObjectAt(JSONArray array, int index) {
        try {
            return array.getJSONObject(index);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * jsonToMap
     *
     * @param @param  jo
     * @param @return
     * @return Map<String,Object>
     * @throws
     * @Title: jsonToMap
     * @Description: TODO
     */
    public static Map<String, Object> jsonToMap(JSONObject jo) {
        if (jo == null)
            return null;
        Map<String, Object> map = new HashMap<String, Object>();
        for (@SuppressWarnings("unchecked")
             Iterator<String> iterator = jo.keys(); iterator.hasNext(); ) {
            String key = (String) iterator.next();
            try {
                Object value = jo.get(key);
                if (!(value instanceof JSONObject)
                        && !(value instanceof JSONArray)) {
                    map.put(key, value);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    /**
     * isEmpty
     *
     * @param @param  array
     * @param @return
     * @return boolean
     * @throws
     * @Title: isEmpty
     * @Description: TODO
     */
    public static boolean isEmpty(JSONArray array) {
        return array == null || array.length() == 0;
    }

    /**
     * put
     *
     * @param @param jo
     * @param @param key
     * @param @param value
     * @return void
     * @throws
     * @Title: put
     * @Description: TODO
     */
    public static void put(JSONObject jo, String key, Object value) {
        if (jo == null || TextUtils.isEmpty(key))
            return;
        try {
            jo.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * add
     *
     * @param @param array
     * @param @param value
     * @return void
     * @throws
     * @Title: add
     * @Description: TODO
     */
    public static void add(JSONArray array, Object value) {
        if (array == null || value == null)
            return;
        array.put(value);
    }

}
