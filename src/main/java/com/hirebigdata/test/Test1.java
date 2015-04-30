package com.hirebigdata.test;

import com.hirebigdata.utils.LoginManager;

import java.util.Map;

/**
 * Created by Administrator on 2015/4/30.
 */
public class Test1 {
    public static void main(String[] args) {
        LoginManager loginManager = new LoginManager();
        Map<String, String> login = loginManager.login("成都数之联科技", "cdsz500", "linxiaohua87", "");
        String s = Test1.cookiesMapToString(login);
    }

    public static String cookiesMapToString(Map<String, String> map) {
        StringBuilder result = new StringBuilder();
        if (map.size() > 0) {
            for (Map.Entry<String, String> e : map.entrySet()) {
                result.append(e.getKey() + "=" + e.getValue() + ";");
            }
            return result.toString();
        } else {
            return "";
        }
    }
}
