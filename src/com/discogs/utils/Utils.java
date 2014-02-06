package com.discogs.utils;

import android.content.Context;
import android.net.ConnectivityManager;

public class Utils {
    public static String splitToComponentTimes(long seconds) {
        int hours = (int) seconds / 3600;
        int remainder = (int) seconds - hours * 3600;
        int mins = remainder / 60;
        remainder = remainder - mins * 60;
        int secs = remainder;

        StringBuffer stringBuffer = new StringBuffer();

        if (hours > 0) {
            if (hours < 10) {
                stringBuffer.append("0");
            }

            stringBuffer.append(String.valueOf(hours));
            stringBuffer.append(":");
        }

        if (mins > 0) {
            if (mins < 10) {
                stringBuffer.append("0");
            }

            stringBuffer.append(String.valueOf(mins));
            stringBuffer.append(":");
        } else {
            stringBuffer.append("00");
            stringBuffer.append(":");
        }

        if (secs > 0) {
            if (secs < 10) {
                stringBuffer.append("0");
            }

            stringBuffer.append(String.valueOf(secs));
        } else {
            stringBuffer.append("00");
        }

        return stringBuffer.toString();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isAvailable()
                && connectivityManager.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            return false;
        }
    }
}