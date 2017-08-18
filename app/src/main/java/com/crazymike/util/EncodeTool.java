package com.crazymike.util;

import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Elliot on 2017/5/18.
 */

public class EncodeTool {

    private static final String TAG = EncodeTool.class.getSimpleName();

    public static String toMD5String(String string) {
        String strEncode = "";
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] bytes = messageDigest.digest(string.getBytes());
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xFF);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                strEncode += temp;
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, e.toString());
        }
        return strEncode;
    }
}
