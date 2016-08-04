package com.logzc.webzic.util;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by lishuang on 2016/7/18.
 */
public class StringUtil {

    public static boolean isNotEmpty(final CharSequence cs) {
        return StringUtils.isNotEmpty(cs);
    }


    public static String trimAllWhitespace(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }

        int length = str.length();

        StringBuilder stringBuilder = new StringBuilder(str.length());

        for (int i = 0; i < length; i++) {
            char c = str.charAt(i);
            if (!Character.isWhitespace(c)) {
                stringBuilder.append(c);
            }
        }
        return stringBuilder.toString();
    }


}
